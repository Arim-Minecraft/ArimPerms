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

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jdt.annotation.Nullable;

import space.arim.perms.api.Group;
import space.arim.perms.api.GroupManager;

public class Groups implements GroupManager {

	private final ConcurrentHashMap<String, Group> groups = new ConcurrentHashMap<String, Group>();
	private Collection<Group> groupsView;
	
	@Override
	public Group getGroup(String id) {
		return groups.computeIfAbsent(id, (groupId) -> new GroupInfo(groupId));
	}
	
	@Override
	@Nullable
	public Group getPossibleGroup(String id) {
		return groups.get(id);
	}
	
	@Override
	public boolean addGroup(Group group) {
		return groups.put(group.getId(), group) != null;
	}
	
	@Override
	public Collection<Group> getGroups() {
		return (groupsView != null) ? groupsView : (groupsView = Collections.unmodifiableCollection(groups.values()));
	}
	
}
