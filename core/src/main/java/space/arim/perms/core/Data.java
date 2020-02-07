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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import space.arim.universal.util.collections.CollectionsUtil;

import space.arim.api.util.StringsUtil;

import space.arim.perms.api.ArimPerms;
import space.arim.perms.api.DataManager;
import space.arim.perms.api.Group;
import space.arim.perms.api.User;

public class Data implements DataManager {

	private final ArimPerms core;
	
	private StorageMode mode;
	private Backend backend;
	private String table_prefix;
	
	public Data(ArimPerms core) {
		this.core = core;
	}
	
	private enum StorageMode {
		FLATFILE,
		FILETREE,
		MYSQL;
	}
	
	private Backend getBackend() {
		if (backend == null) {
			switch (mode) {
			case FLATFILE:
				backend = new FlatFileBackend(core);
				break;
			case FILETREE:
				backend = new FileTreeBackend(core);
				break;
			case MYSQL:
				backend = new MySqlBackend(core, table_prefix);
				break;
			default:
				throw new IllegalStateException("Invalid backend enum!");
			}
			if (!backend.start()) {
				backend.close();
				backend = new FlatFileBackend(core);
			}
		}
		return backend;
	}
	
	private Backend getAltBackend() {
		return mode.equals(StorageMode.FLATFILE) ? null : new FlatFileBackend(core);
	}
	
	private Stream<RawGroup> loadRawGroups() {
		Set<RawGroup> rawGroups = getBackend().loadRawGroups();
		if (rawGroups == null) {
			Backend alt = getAltBackend();
			if (alt != null) {
				rawGroups = alt.loadRawGroups();
			}
		}
		if (rawGroups != null) {
			return rawGroups.stream();
		}
		core.logs().warning("Could not load any groups.");
		return Stream.empty();
	}
	
	private Stream<RawUser> loadRawUsers() {
		Set<RawUser> rawUsers = getBackend().loadRawUsers();
		if (rawUsers == null) {
			Backend alt = getAltBackend();
			if (alt != null) {
				rawUsers = alt.loadRawUsers();
			}
		}
		if (rawUsers != null) {
			return rawUsers.stream();
		}
		core.logs().warning("Could not load any users.");
		return Stream.empty();
	}
	
	private void saveGroups(Collection<Group> groups) {
		boolean success = getBackend().saveRawGroups(groups.stream().map(this::convertGroupToRaw));
		if (!success) {
			Backend alt = getAltBackend();
			if (alt != null) {
				success = alt.saveRawGroups(groups.stream().map(this::convertGroupToRaw));
			}
		}
		if (!success) {
			core.logs().warning("Could not save your groups in any format.");
		}
	}
	
	private void saveUsers(Collection<User> users) {
		boolean success = getBackend().saveRawUsers(users.stream().map(this::convertUserToRaw).filter(Objects::nonNull));
		if (!success) {
			Backend alt = getAltBackend();
			if (alt != null) {
				success = alt.saveRawUsers(users.stream().map(this::convertUserToRaw).filter(Objects::nonNull));
			}
		}
		if (!success) {
			core.logs().warning("Could not save your users in any format.");
		}
	}
	
	private StorageMode getStorageMode() {
		String storageMode = core.config().getString("storage.mode").toLowerCase();
		switch (storageMode) {
		case "sql":
		case "mysql":
			return StorageMode.MYSQL;
		case "tree":
		case "filetree":
			return StorageMode.FILETREE;
		case "flatfile":
		case "file":
			return StorageMode.FLATFILE;
		default:
			core.logs().warning("Invalid storage mode specified: " + storageMode + " (Available modes are mysql, file, and filetree). Defaulting to FILE...");
			return StorageMode.FLATFILE;
		}
	}
	
	@Override
	public void reload(boolean first) {
		if (first) {
			mode = getStorageMode();
			table_prefix = core.config().getString("storage.mysql.table-prefix");
		}
	}
	
	private void closeBackend() {
		if (backend != null) {
			backend.close();
			backend = null;
		}
	}
	
	@Override
	public void loadAll() {
		loadRawGroups().forEach(this::addGroupFromRaw);
		loadRawUsers().forEach(this::addUserFromRaw);
		closeBackend();
	}
	
	@Override
	public void saveAll() {
		saveUsers(core.users().getUsers());
		saveGroups(core.groups().getGroups());
		closeBackend();
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
		return StringsUtil.concat(CollectionsUtil.wrapAll(group.getCategories().toArray(new String[] {}), (category) -> (category == null ? "<MAIN>" : category) + ":" + StringsUtil.concat(group.getPermissions(category), ';')), ',');
	}
	
	private static String getGroups(User user) {
		return StringsUtil.concat(CollectionsUtil.convertAll(user.getGroups(), (group) -> group.getId()), ','); 
	}
	
	private void addGroupFromRaw(RawGroup groupData) {
		
		Group[] parents = CollectionsUtil.convertAll(groupData.getParents().split(","), core.groups()::getGroup);
		HashMap<String, Set<String>> categoryPermissions = new HashMap<String, Set<String>>();
		
		for (String categoryInfo : groupData.getPermissions().split(",")) {
			String[] categoryData = categoryInfo.split(":");
			Set<String> permissions = ConcurrentHashMap.newKeySet();
			permissions.addAll(Arrays.asList(categoryData[1].split(";")));
			categoryPermissions.put(categoryData[0].equals("<MAIN>") ? null : categoryData[0], permissions);
		}
		
		Group group = core.groups().getGroup(groupData.getId());
		if (group instanceof GroupInfo) {
			GroupInfo groupInfo = (GroupInfo) group;
			groupInfo.setParents(parents);
			categoryPermissions.forEach(groupInfo::setPermissions);
		} else {
			for (Group parent : parents) {
				group.addParent(parent);
			}
			categoryPermissions.forEach(group::addPermissions);
		}
	}
	
	private void addUserFromRaw(RawUser userData) {
		Group[] groups = CollectionsUtil.convertAll(userData.getGroups().split(","), core.groups()::getGroup);
		User user = core.users().getUser(userData.getId());
		if (user instanceof UserInfo) {
			((UserInfo) user).setGroups(groups);
		} else {
			for (Group group : groups) {
				user.addGroup(group);
			}
		}
	}
	
}
