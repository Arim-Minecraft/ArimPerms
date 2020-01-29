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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import space.arim.api.util.FilesUtil;
import space.arim.api.util.StringsUtil;

import space.arim.perms.api.ArimPerms;

class FlatFileBackend extends FileBackend {

	FlatFileBackend(ArimPerms core) {
		super(core);
	}
	
	@Override
	public Set<RawGroup> loadRawGroups() {
		HashSet<RawGroup> rawGroups = new HashSet<RawGroup>();
		if (!FilesUtil.readLines(new File(getDirectory(), "groups.txt"), (line) -> {
			String[] data = line.split("|");
			rawGroups.add(new RawGroup(data[0], data[1], data[2]));
		}, (ex) -> core.logs().exception("Could not load groups from groups.txt!", ex))) {
			return null;
		}
		return rawGroups;
	}
	
	@Override
	public Set<RawUser> loadRawUsers() {
		HashSet<RawUser> rawUsers = new HashSet<RawUser>();
		if (!FilesUtil.readLines(new File(getDirectory(), "users.txt"), (line) -> {
			String[] data = line.split("|");
			RawUser rawUser = RawUser.from(data[0], data[1]);
			if (rawUser != null) {
				rawUsers.add(rawUser);
			}
		}, (ex) -> core.logs().exception("Could not load users from groups.txt!", ex))) {
			return null;
		}
		return rawUsers;
	}
	
	@Override
	public boolean saveRawGroups(Stream<RawGroup> groups) {
		return FilesUtil.writeTo(new File(getDirectory(), "groups.txt"), (writer) -> {
			writer.append(StringsUtil.concat(groups.map(Objects::toString).collect(Collectors.toSet()), '\n'));
		}, (ex) -> core.logs().exception("Could not save groups to groups.txt!", ex));
	}
	
	@Override
	public boolean saveRawUsers(Stream<RawUser> users) {
		return FilesUtil.writeTo(new File(getDirectory(), "users.txt"), (writer) -> {
			writer.append(StringsUtil.concat(users.map(Objects::toString).collect(Collectors.toSet()), '\n'));
		}, (ex) -> core.logs().exception("Could not save users to users.txt!", ex));
	}

}
