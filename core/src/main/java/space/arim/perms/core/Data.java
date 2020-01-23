/* 
 * ArimPerms-core
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * ArimPerms-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ArimPerms-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ArimPerms-core. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.perms.core;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import space.arim.universal.util.collections.CollectionsUtil;
import space.arim.universal.util.function.LazySingleton;

import space.arim.api.sql.ExecutableQuery;
import space.arim.api.util.FilesUtil;
import space.arim.api.util.StringsUtil;

import space.arim.perms.api.ArimPerms;
import space.arim.perms.api.DataManager;
import space.arim.perms.api.Group;
import space.arim.perms.api.User;

public class Data implements DataManager {

	private final ArimPerms core;
	
	private final LazySingleton<File> directory;
	private MySql mysql;
	
	private boolean isMySql = false;
	private String table_prefix;
	
	public Data(ArimPerms core) {
		this.core = core;
		directory = new LazySingleton<File>(() -> {
			File directory = new File(core.getFolder(), "data");
			if (!directory.isDirectory() && !directory.mkdirs()) {
				throw new IllegalStateException("Cannot create data directory!");
			}
			return directory;
		});	
	}
	
	private Set<RawGroup> loadRawGroups() {
		HashSet<RawGroup> rawGroups = new HashSet<RawGroup>();
		if (isMySql) {
			try {
				ResultSet data = mysql.selectionQuery(Query.PreQuery.SELECT_GROUPS.eval(table_prefix));
				while (data.next()) {
					rawGroups.add(new RawGroup(data));
				}
				return rawGroups;
			} catch (SQLException ex) {
				core.logs().exception("Could not load groups from remote database! Using local storage...", ex);
			}
		}
		FilesUtil.readLines(new File(directory.get(), "groups.txt"), (line) -> {
			String[] data = line.split("|");
			rawGroups.add(new RawGroup(data[0], data[1], data[2]));
		}, (ex) -> core.logs().exception("Could not load groups from groups.txt!", ex));
		return rawGroups;
	}
	
	private Set<RawUser> loadRawUsers() {
		HashSet<RawUser> rawUsers = new HashSet<RawUser>();
		if (isMySql) {
			try {
				ResultSet data = mysql.selectionQuery(Query.PreQuery.SELECT_USERS.eval(table_prefix));
				while (data.next()) {
					rawUsers.add(new RawUser(data));
				}
				return rawUsers;
			} catch (SQLException ex) {
				core.logs().exception("Could not load users from remote database! Using local storage...", ex);
			}
		}
		FilesUtil.readLines(new File(directory.get(), "users.txt"), (line) -> {
			String[] data = line.split("|");
			RawUser rawUser = RawUser.from(data[0], data[1]);
			if (rawUser != null) {
				rawUsers.add(rawUser);
			}
		}, (ex) -> core.logs().exception("Could not load users from groups.txt!", ex));
		return rawUsers;
	}
	
	@Override
	public Collection<Group> loadGroups() {
		HashSet<Group> groups = new HashSet<Group>();
		loadRawGroups().forEach((rawGroup) -> groups.add(convertGroupFromRaw(rawGroup)));
		return groups;
	}
	
	@Override
	public Collection<User> loadUsers() {
		HashSet<User> users = new HashSet<User>();
		loadRawUsers().forEach((rawUser) -> users.add(convertUserFromRaw(rawUser)));
		return users;
	}
	
	private void printGroupsToFile(RawGroup[] groups) {
		FilesUtil.writeTo(new File(directory.get(), "groups.txt"), (writer) -> {
			writer.append(StringsUtil.concat(CollectionsUtil.convertAllToString(groups), '\n'));
		}, (ex) -> core.logs().exception("Could not save groups to groups.txt!", ex));
	}
	
	private void printUsersToFile(RawUser[] users) {
		FilesUtil.writeTo(new File(directory.get(), "users.txt"), (writer) -> {
			writer.append(StringsUtil.concat(CollectionsUtil.convertAllToString(users), '\n'));
		}, (ex) -> core.logs().exception("Could not save users to users.txt!", ex));
	}
	
	@Override
	public void saveGroups(Collection<Group> groups) {
		RawGroup[] groupData = CollectionsUtil.convertAll(groups.toArray(new Group[] {}), this::convertGroupToRaw);
		if (isMySql) {
			try {
				mysql.executionQueries(CollectionsUtil.convertAll(groupData, this::executeRawGroup));
				return;
			} catch (SQLException ex) {
				core.logs().exception("Could not save groups to remote database! Using local storage...", ex);
			}
		}
		printGroupsToFile(groupData);
	}
	
	@Override
	public void saveUsers(Collection<User> users) {
		RawUser[] userData = CollectionsUtil.convertAll(users.toArray(new User[] {}), this::convertUserToRaw);
		if (isMySql) {
			try {
				mysql.executionQueries(CollectionsUtil.convertAll(userData, this::executeRawUser));
				return;
			} catch (SQLException ex) {
				core.logs().exception("Could not save users to remote database! Using local storage...", ex);
			}
		}
		printUsersToFile(userData);
	}
	
	private ExecutableQuery executeRawGroup(RawGroup group) {
		return group.query().convertToExecutable(table_prefix);
	}
	
	private ExecutableQuery executeRawUser(RawUser user) {
		return user == null ? null : user.query().convertToExecutable(table_prefix);
	}
	
	@Override
	public void readyDb() {
		if (isMySql) {
			mysql = new MySql(core);
			if (!mysql.start()) {
				isMySql = false;
				mysql.close();
				core.logs().info("Falling back to file storage mode...");
			}
		}
	}
	
	@Override
	public void closeDb() {
		if (isMySql) {
			mysql.close();
		}
	}
	
	@Override
	public void reload(boolean first) {
		isMySql = !core.config().getString("storage.mode").equalsIgnoreCase("file");
		table_prefix = core.config().getString("storage.mysql.table-prefix");
	}
	
	@Override
	public void close() {
		
	}
	
	private RawGroup convertGroupToRaw(Group group) {
		return new RawGroup(group.getId(), getParents(group), getPermissions(group));
	}
	
	private RawUser convertUserToRaw(User user) {
		return RawUser.from(user.getId(), getGroups(user));
	}
	
	private static String getParents(Group group) {
		return StringsUtil.concat(CollectionsUtil.convertAll(group.getParents(), (parent) -> parent.getId()), ',');
	}
	
	private static String getPermissions(Group group) {
		return StringsUtil.concat(CollectionsUtil.wrapAll(group.getWorlds().toArray(new String[] {}), (world) -> {
			if (world == null) {
				world = "<MAIN>";
			}
			return world + ":" + StringsUtil.concat(group.getPermissions(world), ';');
		}), ',');
	}
	
	private static String getGroups(User user) {
		return StringsUtil.concat(CollectionsUtil.convertAll(user.getGroups(), (group) -> group.getId()), ','); 
	}
	
	private Group convertGroupFromRaw(RawGroup groupData) {
		Group[] parents = CollectionsUtil.convertAll(groupData.getParents().split(","), (groupId) -> core.groups().getGroup(groupId));
		
		HashMap<String, Set<String>> worldPermissions = new HashMap<String, Set<String>>();
		String[] worldsArray = groupData.getPermissions().split(",");
		for (String worldInfo : worldsArray) {
			String[] worldData = worldInfo.split(":");
			Set<String> permissions = ConcurrentHashMap.newKeySet();
			for (String permission : worldData[1].split(";")) {
				permissions.add(permission);
			}
			worldPermissions.put(worldData[0].equals("<MAIN>") ? null : worldData[0], permissions);
		}
		
		Group group = core.groups().getGroup(groupData.getId());
		if (group instanceof GroupInfo) {
			GroupInfo groupInfo = (GroupInfo) group;
			groupInfo.setParents(parents);
			worldPermissions.forEach(groupInfo::setPermissions);
		} else {
			for (Group parent : parents) {
				group.addParent(parent);
			}
			worldPermissions.forEach(group::addPermissions);
		}
		return group;
	}
	
	private User convertUserFromRaw(RawUser userData) {
		Group[] groups = CollectionsUtil.convertAll(userData.getGroups().split(","), (groupId) -> core.groups().getGroup(groupId));
		User user = core.users().getUser(userData.getId());
		if (user instanceof UserInfo) {
			((UserInfo) user).setGroups(groups);
		} else {
			for (Group group : groups) {
				user.addGroup(group);
			}
		}
		return user;
	}
	
}
