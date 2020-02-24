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
package space.arim.perms.api;

import java.util.Collection;

import org.eclipse.jdt.annotation.Nullable;

import space.arim.universal.util.collections.CollectionsUtil;

/**
 * A permissions group. <br>
 * <br>
 * <b>Specifications</b>: <br>
 * * {@link #getId()} must return a unique group ID. <br>
 * * <code>#equals(Object object)</code> <b>MUST</b> be overriden <b>AND</b> check for equivalency using <code>#getId()</code> <br>
 * * <code>{@link #hashCode()}</code> should likewise be overriden and implemented based on <code>#getId()</code> <br>
 * * {@link #hasPermission(String, String)} should check whether the group has the permission ONLY for the category provided.
 * It should NOT determine whether the group has permission for the category or the general category.
 * 
 * @author A248
 *
 */
public interface Group extends Permissible {

	/**
	 * Gets the ID of this group. Should be unique
	 * 
	 * @return the String based id
	 */
	@Override
	String getId();
	
	/**
	 * Gets groups from which this group inherits. <br>
	 * The returned array is a copy; modifying it will not affect this Group.
	 * 
	 * @return all parent groups
	 */
	Group[] getParents();
	
	/**
	 * Adds another group as a parent of this one.
	 * 
	 * @param group the group to add as a parent
	 * @return false if this group already has the specified group as a parent, true otherwise
	 */
	boolean addParent(Group parent);
	
	/**
	 * Removes another group as a parent of this one.
	 * 
	 * @param group the group to remove as a parent
	 * @return false if this group did not have the specified group as a parent, true otherwise
	 */
	boolean removeParent(Group parent);
	
	/**
	 * Gets all permissions for the group
	 * 
	 * @return all the permissions
	 */
	default Collection<String> getPermissions() {
		return getPermissions(null);
	}
	
	/**
	 * Gets all permissions for the group with optional category
	 * 
	 * @param category the category
	 * @return all the permissions
	 */
	Collection<String> getPermissions(@Nullable String category);
	
	/**
	 * Returns this group's current calculated parent groups, including itself
	 * 
	 * @return a collection of all applicable groups
	 */
	Collection<Group> getEffectiveParents();
	
	/**
	 * Returns all categories tracked by this Group.
	 * 
	 * @return a collection of all categories for which there are specific permissions
	 */
	Collection<String> getCategories();
	
	/**
	 * Adds a permission to the group
	 * 
	 * @param permission the permission
	 * @return true if and only if the group's permissions changed as a result of the call
	 */
	default boolean addPermission(String permission) {
		return addPermission(permission, null);
	}
	
	/**
	 * Adds a permission to the group with optional category
	 * 
	 * @param permission the permission
	 * @param category the category
	 * @return true if and only if the group's permissions changed as a result of the call
	 */
	boolean addPermission(String permission, @Nullable String category);
	
	/**
	 * Adds all permissions to the group
	 * 
	 * @param category the category
	 * @return true if and only if the group's permissions changed as a result of the call
	 */
	default boolean addPermissions(Collection<String> permissions) {
		return addPermissions(null, permissions);
	}
	
	/**
	 * Adds all permissions to the group with optional category
	 * 
	 * @param category the category
	 * @param permissions the permissions
	 * @return true if and only if the group's permissions changed as a result of the call
	 */
	boolean addPermissions(@Nullable String category, Collection<String> permissions);
	
	/**
	 * Gets whether this group has a specific permission
	 * 
	 * @param permission the permission
	 * @return true if and only if the group has the permission
	 */
	@Override
	default boolean hasPermission(String permission) {
		return hasPermission(permission, null);
	}
	
	/**
	 * Gets whether this group has a specific permission with optional category
	 * 
	 * @param permission the permission
	 * @param category the category
	 * @return true if and only if the group has the permission
	 */
	@Override
	default boolean hasPermission(String permission, @Nullable String category) {
		return CollectionsUtil.checkForAnyMatches(getEffectiveParents(), (parent) -> parent.getPermissions(category).contains(permission));
	}
	
	/**
	 * Remove a permission from the group
	 * 
	 * @param permission the permission
	 * @return true if and only if the group's permissions changed as a result of the call
	 */
	default boolean removePermission(String permission) {
		return removePermission(permission, null);
	}
	
	/**
	 * Remove a permission from the group with optional category
	 * 
	 * @param permission the permission
	 * @param category the category
	 * @return true if and only if the group's permissions changed as a result of the call
	 */
	boolean removePermission(String permission, @Nullable String category);
	
	/**
	 * Removes all specified permissions from the group
	 * 
	 * @param permission the permission
	 * @return true if and only if the group's permissions changed as a result of the call
	 */
	default boolean removePermissions(Collection<String> permissions) {
		return removePermissions(null, permissions);
	}
	
	/**
	 * Removes all specified permissions from the group with optional category
	 * 
	 * @param category the category
	 * @param permissions the permissions
	 * @return true if and only if the group's permissions changed as a result of the call
	 */
	boolean removePermissions(@Nullable String category, Collection<String> permissions);
	
	/**
	 * Removes all permissions from the group
	 * 
	 * @return true if and only if the group's permissions changed as a result of the call
	 */
	default boolean clearPermissions() {
		return clearPermissions(null);
	}
	
	/**
	 * Removes all permissions from the group with optional category
	 * 
	 * @param category the category
	 * @return true if and only if the group's permissions changed as a result of the call
	 */
	boolean clearPermissions(@Nullable String category);
	
	/**
	 * Instructs this group to recalculate its effective parents. <br>
	 * See {@link #getEffectiveParents()}
	 * 
	 */
	void recalculate();
	
}
