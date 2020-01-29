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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jdt.annotation.Nullable;

import space.arim.universal.util.collections.ArraysUtil;
import space.arim.universal.util.collections.CollectionsUtil;

import space.arim.perms.api.Group;
import space.arim.perms.api.User;

public class UserInfo implements User {

	private final String id;
	private Group[] groups;
	
	private final ConcurrentHashMap<String, Collection<String>> effective = new ConcurrentHashMap<String, Collection<String>>();
	
	UserInfo(String id, Group...groups){
		this.id = id;
		this.groups = groups;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public Group[] getGroups() {
		return groups;
	}
	
	@Override
	public Collection<String> getEffectivePermissions(@Nullable String world) {
		return effective.getOrDefault(world, Collections.emptySet());
	}
	
	void setGroups(Group[] groups) {
		this.groups = groups;
	}
	
	@Override
	public boolean addGroup(Group group) {
		Group[] previous = groups;
		groups = ArraysUtil.addIfNotPresent(previous, group);
		return groups != previous;
	}
	
	@Override
	public boolean removeGroup(Group group) {
		Group[] previous = groups;
		groups = ArraysUtil.remove(previous, group);
		return groups != previous;
	}
	
	@Override
	public boolean hasPermission(String permission, @Nullable String world) {
		return CollectionsUtil.checkForAnyMatches(getEffectivePermissions(world), (checkPerm) -> ArimPermsPlugin.matches(permission, checkPerm));
	}
	
	@Override
	public void recalculate(@Nullable String world) {
		Set<String> effective = new HashSet<String>();
		for (Group group : getGroups()) {
			group.getEffectiveParents().forEach((parent) -> {
				effective.addAll(parent.getPermissions(world));
			});
		}
		this.effective.put(world, Collections.unmodifiableSet(effective));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object object) {
		return object instanceof User && equals((User) object);
	}
	
}
