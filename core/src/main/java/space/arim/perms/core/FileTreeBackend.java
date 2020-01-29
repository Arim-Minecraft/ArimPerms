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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import space.arim.api.util.FilesUtil;

import space.arim.perms.api.ArimPerms;

class FileTreeBackend extends FileBackend {

	FileTreeBackend(ArimPerms core) {
		super(core);
	}
	
	@Override
	public Set<RawGroup> loadRawGroups() {
		HashSet<RawGroup> rawGroups = new HashSet<RawGroup>();
		File[] files = (new File(getDirectory(), "groups")).listFiles();
		if (files != null) {
			for (File file : files) {
				if (!FilesUtil.readLines(file, (line) -> {
					String[] data = line.split("|");
					rawGroups.add(new RawGroup(file.getName(), data[0], data[1]));
				}, (ex) -> core.logs().exception("Could not load groups from filetree!", ex))) {
					return null;
				}
			}
		}
		return rawGroups;
	}
	
	@Override
	public Set<RawUser> loadRawUsers() {
		HashSet<RawUser> rawUsers = new HashSet<RawUser>();
		File[] files = (new File(getDirectory(), "users")).listFiles();
		if (files != null) {
			for (File file : files) {
				if (!FilesUtil.readLines(file, (line) -> {
					RawUser rawUser = RawUser.from(file.getName(), line);
					if (rawUser != null) {
						rawUsers.add(rawUser);
					}
				}, (ex) -> core.logs().exception("Could not load users from filetree!", ex))) {
					return null;
				}
			}
		}
		return rawUsers;
	}
	
	@Override
	public boolean saveRawGroups(Stream<RawGroup> groups) {
		return groups.allMatch((group) -> FilesUtil.writeTo(new File(getDirectory(), "groups/" + group.getId()), (writer) -> {
			writer.append(group.toStringWithoutId());
		}, (ex) -> core.logs().exception("Could not save group " + group.getId() + " to filetree!", ex)));
	}
	
	@Override
	public boolean saveRawUsers(Stream<RawUser> users) {
		return users.allMatch((user) -> FilesUtil.writeTo(new File(getDirectory(), "users/" + user.getId()), (writer) -> {
			writer.append(user.getGroups());
		}, (ex) -> core.logs().exception("Could not save user " + user.getId() + " to filetree!", ex)));
	}
	
}
