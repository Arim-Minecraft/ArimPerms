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

public class GroupInfo implements Group {

	private final String id;
	private Group[] parents;
	
	private final ConcurrentHashMap<String, Set<String>> permissions = new ConcurrentHashMap<String, Set<String>>();
	
	private Set<Group> effective;
	
	public GroupInfo(String id, Group...parents) {
		this.id = id;
		this.parents = parents;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public Group[] getParents() {
		return parents;
	}
	
	@Override
	public Collection<String> getPermissions(@Nullable String world) {
		return Collections.unmodifiableSet(permissions.getOrDefault(world, Collections.emptySet()));
	}
	
	@Override
	public Collection<Group> getEffectiveParents() {
		return effective != null ? effective : Collections.emptySet();
	}
	
	@Override
	public Collection<String> getWorlds() {
		return Collections.unmodifiableSet(permissions.keySet());
	}
	
	void setParents(Group[] parents) {
		this.parents = parents;
	}
	
	void setPermissions(@Nullable String world, Set<String> permissions) {
		this.permissions.put(world, permissions);
	}
	
	@Override
	public boolean addPermission(String permission, @Nullable String world) {
		return permissions.computeIfAbsent(world, (w) -> ConcurrentHashMap.newKeySet()).add(permission);
	}
	
	@Override
	public boolean addPermissions(@Nullable String world, Collection<String> permissions) {
		return this.permissions.computeIfAbsent(world, (w) -> ConcurrentHashMap.newKeySet()).addAll(permissions);
	}
	
	@Override
	public boolean hasPermission(String permission, @Nullable String world) {
		return CollectionsUtil.checkForAnyMatches(getPermissions(world), (checkPerm) -> ArimPermsPlugin.matches(permission, checkPerm));
	}
	
	@Override
	public boolean removePermission(String permission, @Nullable String world) {
		Set<String> perms = permissions.get(world);
		return perms != null && perms.remove(permission);
	}
	
	@Override
	public boolean removePermissions(@Nullable String world, Collection<String> permissions) {
		Set<String> perms = this.permissions.get(world);
		return perms != null && perms.removeAll(permissions);
	}
	
	@Override
	public boolean addParent(Group parent) {
		Group[] previous = parents;
		parents = ArraysUtil.addIfNotPresent(previous, parent);
		return parents != previous;
	}
	
	@Override
	public boolean removeParent(Group parent) {
		Group[] previous = parents;
		parents = ArraysUtil.remove(parents, parent);
		return parents != previous;
	}
	
	private static void addGroupsRecursive(Set<Group> existing, Group group, int recursion) {
		if (recursion > ArimPermsPlugin.MAX_RECURSION_DEPTH) {
			throw new IllegalStateException("Group recursion checking entered depth " + ArimPermsPlugin.MAX_RECURSION_DEPTH + "!");
		}
		for (Group parent : group.getParents()) {
			existing.add(parent);
			addGroupsRecursive(existing, parent, ++recursion);
		}
	}
	
	@Override
	public void recalculate() {
		Set<Group> groups = new HashSet<Group>();
		groups.add(this);
		for (Group parent : getParents()) {
			groups.add(parent);
			addGroupsRecursive(groups, parent, 0);
		}
		effective = Collections.unmodifiableSet(groups);
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
		return object instanceof Group && equals((Group) object);
	}
	
}
