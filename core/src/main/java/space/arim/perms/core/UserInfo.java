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

import space.arim.perms.api.Group;
import space.arim.perms.api.User;

public class UserInfo implements User {

	private final String id;
	
	private final Set<Group> groups = ConcurrentHashMap.newKeySet();
	private Set<Group> groupsView;
	
	private final ConcurrentHashMap<String, Set<String>> effective = new ConcurrentHashMap<String, Set<String>>();
	
	UserInfo(String id){
		this.id = id;
	}
	
	UserInfo(String id, Collection<Group> groups){
		this(id);
		this.groups.addAll(groups);
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public Set<Group> getGroups() {
		return (groupsView != null) ? groupsView : (groupsView = Collections.unmodifiableSet(groups));
	}
	
	@Override
	public Collection<String> getEffectivePermissions(@Nullable String category) {
		return effective.getOrDefault(category, Collections.emptySet());
	}
	
	void setGroups(Collection<Group> groups) {
		this.groups.addAll(groups);
		this.groups.retainAll(groups);
	}
	
	@Override
	public boolean addGroup(Group group) {
		return groups.add(group);
	}
	
	@Override
	public boolean removeGroup(Group group) {
		return groups.remove(group);
	}
	
	@Override
	public boolean hasPermission(String permission, @Nullable String category) {
		return getEffectivePermissions(category).stream().anyMatch((checkPerm) -> ArimPermsPlugin.matches(permission, checkPerm));
	}
	
	@Override
	public void recalculate(@Nullable String category) {
		Set<String> effective = new HashSet<String>();
		for (Group group : getGroups()) {
			group.getEffectiveParents().forEach((parent) -> {
				effective.addAll(parent.getPermissions(category));
			});
		}
		this.effective.put(category, Collections.unmodifiableSet(effective));
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
		return object instanceof User && getId().equals(((User) object).getId());
	}
	
}
