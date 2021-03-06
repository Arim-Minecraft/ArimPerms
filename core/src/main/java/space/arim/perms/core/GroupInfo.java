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

import space.arim.universal.util.proxy.DynamicUnmodifiableCollection;

import space.arim.perms.api.Group;

public class GroupInfo implements Group {

	private final String id;
	
	private final Set<Group> parents = ConcurrentHashMap.newKeySet();
	private volatile Set<Group> parentsView;
	
	private final ConcurrentHashMap<String, Set<String>> permissions = new ConcurrentHashMap<String, Set<String>>();
	private final ConcurrentHashMap<String, Collection<String>> permissionsView = new ConcurrentHashMap<String, Collection<String>>();
	private volatile Set<String> categoriesView;
	
	private volatile Set<Group> effective;
	
	GroupInfo(String id) {
		this.id = id;
	}
	
	GroupInfo(String id, Collection<Group> parents) {
		this(id);
		this.parents.addAll(parents);
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public Set<Group> getParents() {
		return (parentsView != null) ? parentsView : (parentsView = Collections.unmodifiableSet(parents));
	}
	
	/**
	 * Directly gets the permissions of the group for the category. <br>
	 * Rather than returning <code>null</code>, an empty set is returned if
	 * there are no permissions set for the category specified.
	 * 
	 * @param category the category
	 * @return the permissions for that category
	 */
	Set<String> getMutablePerms(@Nullable String category) {
		return permissions.getOrDefault(category, Collections.emptySet());
	}
	
	@Override
	public Collection<String> getPermissions(@Nullable String category) {
		return permissionsView.computeIfAbsent(category, (cat) -> new DynamicUnmodifiableCollection<String>(() -> getMutablePerms(cat)));
	}
	
	@Override
	public Collection<Group> getEffectiveParents() {
		return effective != null ? effective : Collections.emptySet();
	}
	
	@Override
	public Collection<String> getCategories() {
		return (categoriesView != null) ? categoriesView : (categoriesView = Collections.unmodifiableSet(permissions.keySet()));
	}
	
	/**
	 * Sets the direct parent groups of this Group to those in given collection <br>
	 * Unlike {@link #setPermissions(String, Set)}, the specified collection parameter need have no contract.
	 * The collection is read, the groups are added, and the collection is discarded.
	 * 
	 * @param parents the parent groups
	 */
	void setParents(Collection<Group> parents) {
		this.parents.addAll(parents);
		this.parents.retainAll(parents);
	}
	
	/**
	 * Sets the permissions of the Group to the permissions set with optional category <br>
	 * <br>
	 * <b>Notice</b>: The set is NOT copied. It is directly inserted into this Group's data.
	 * <i>Therefore, please use {@link ConcurrentHashMap#newKeySet()} or some other set supporting atomic reads and writes</i>
	 * 
	 * @param category the category
	 * @param permissions the permissions set, which should support atomic reads and writes
	 */
	void setPermissions(@Nullable String category, Set<String> permissions) {
		this.permissions.put(category, permissions);
	}
	
	@Override
	public boolean addPermission(String permission, @Nullable String category) {
		return permissions.computeIfAbsent(category, (c) -> ConcurrentHashMap.newKeySet()).add(permission);
	}
	
	@Override
	public boolean addPermissions(@Nullable String category, Collection<String> permissions) {
		return this.permissions.computeIfAbsent(category, (c) -> ConcurrentHashMap.newKeySet()).addAll(permissions);
	}
	
	@Override
	public boolean hasPermission(String permission, @Nullable String category) {
		return permissions.getOrDefault(category, Collections.emptySet()).stream().anyMatch((checkPerm) -> ArimPermsPlugin.matches(permission, checkPerm));
	}
	
	@Override
	public boolean removePermission(String permission, @Nullable String category) {
		Set<String> perms = permissions.get(category);
		return perms != null && perms.remove(permission);
	}
	
	@Override
	public boolean removePermissions(@Nullable String category, Collection<String> permissions) {
		Set<String> perms = this.permissions.get(category);
		return perms != null && perms.removeAll(permissions);
	}
	
	@Override
	public boolean clearPermissions(@Nullable String category) {
		Set<String> perms = permissions.get(category);
		if (perms == null || perms.isEmpty()) {
			return false;
		}
		perms.clear();
		return true;
	}
	
	@Override
	public boolean addParent(Group parent) {
		return parents.add(parent);
	}
	
	@Override
	public boolean removeParent(Group parent) {
		return parents.remove(parent);
	}
	
	private static void addGroupsRecursive(Set<Group> existing, Group group, int recursion) {
		if (recursion < ArimPermsPlugin.MAX_RECURSION_DEPTH) {
			existing.add(group);
			for (Group parent : group.getParents()) {
				addGroupsRecursive(existing, parent, ++recursion);
			}
		}
	}
	
	@Override
	public void recalculate() {
		// HashSet can be read from, but not written to, concurrently
		Set<Group> groups = new HashSet<Group>();
		addGroupsRecursive(groups, this, 0);
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
		return object instanceof Group && getId().equals(((Group) object).getId());
	}
	
}
