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
 * A permissions group.
 * 
 * @author A248
 *
 */
public interface Group {

	/**
	 * Gets the ID of this group. Should be unique
	 * 
	 * @return the String based id
	 */
	String getId();
	
	/**
	 * Gets groups from which this group inherits.
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
	 * Gets all permissions for the group with optional world
	 * 
	 * @param world the world
	 * @return all the permissions
	 */
	Collection<String> getPermissions(@Nullable String world);
	
	/**
	 * Returns this group's current calculated parent groups, including itself
	 * 
	 * @return a collection of all applicable groups
	 */
	Collection<Group> getEffectiveParents();
	
	/**
	 * Returns all worlds tracked by this Group.
	 * 
	 * @return a collection of all worlds for which there are world specific permissions
	 */
	Collection<String> getWorlds();
	
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
	 * Adds a permission to the group with optional world
	 * 
	 * @param permission the permission
	 * @param world the world
	 * @return true if and only if the group's permissions changed as a result of the call
	 */
	boolean addPermission(String permission, @Nullable String world);
	
	/**
	 * Adds all permissions to the group
	 * 
	 * @param world the world
	 * @return true if and only if the group's permissions changed as a result of the call
	 */
	default boolean addPermissions(Collection<String> permissions) {
		return addPermissions(null, permissions);
	}
	
	/**
	 * Adds all permissions to the group with optional world
	 * 
	 * @param world the world
	 * @param permissions the permissions
	 * @return true if and only if the group's permissions changed as a result of the call
	 */
	boolean addPermissions(@Nullable String world, Collection<String> permissions);
	
	/**
	 * Gets whether this group has a specific permission
	 * 
	 * @param permission the permission
	 * @return true if and only if the group has the permission
	 */
	default boolean hasPermission(String permission) {
		return hasPermission(permission, null);
	}
	
	/**
	 * Gets whether this group has a specific permission with optional world
	 * 
	 * @param permission the permission
	 * @param world the world
	 * @return true if and only if the group has the permission
	 */
	default boolean hasPermission(String permission, @Nullable String world) {
		return CollectionsUtil.checkForAnyMatches(getEffectiveParents(), (parent) -> parent.getPermissions(world).contains(permission));
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
	 * Remove a permission from the group with optional world
	 * 
	 * @param permission the permission
	 * @param world the world
	 * @return true if and only if the group's permissions changed as a result of the call
	 */
	boolean removePermission(String permission, @Nullable String world);
	
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
	 * Removes all specified permissions from the group with optional world
	 * 
	 * @param world the world
	 * @param permissions the permissions
	 * @return true if and only if the group's permissions changed as a result of the call
	 */
	boolean removePermissions(@Nullable String world, Collection<String> permissions);
	
	/**
	 * Removes all permissions from the group
	 * 
	 * @return true if and only if the group's permissions changed as a result of the call
	 */
	default boolean clearPermissions() {
		return clearPermissions(null);
	}
	
	/**
	 * Removes all permissions from the group with optional world
	 * 
	 * @param world the world
	 * @return true if and only if the group's permissions changed as a result of the call
	 */
	boolean clearPermissions(@Nullable String world);
	
	/**
	 * Instructs this group to recalculate its effective parents. <br>
	 * See {@link #getEffectiveParents()}
	 * 
	 */
	void recalculate();
	
	/**
	 * Whether this group is equal to another. Uses {@link #getId()}
	 * 
	 * @param group the other group
	 * @return true if and only if the IDs are equal according to Object#equals
	 */
	default boolean equals(Group group) {
		return group.getId().equalsIgnoreCase(getId());
	}
	
}
