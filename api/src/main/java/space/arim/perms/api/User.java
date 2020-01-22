/* 
 * ArimPerms-api
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * ArimPerms-api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ArimPerms-api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ArimPerms-api. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.perms.api;

import java.util.Collection;

import org.eclipse.jdt.annotation.Nullable;

/**
 * A permissions user.
 * 
 * @author A248
 *
 */
public interface User {

	/**
	 * Gets the ID of this user. Should be unique
	 * 
	 * @return the String based id
	 */
	String getId();
	
	/**
	 * Gets groups from which this user inherits
	 * 
	 * @return all applicable groups
	 */
	Group[] getGroups();
	
	/**
	 * Adds this user to the specified group.
	 * 
	 * @param group the group to which the user will be added
	 * @return false if the user is already in the group, true otherwise
	 */
	boolean addGroup(Group group);
	
	/**
	 * Removes this user from the specified group.
	 * 
	 * @param group the group from which the user will be removed
	 * @return false if the user was not in the group, true otherwise
	 */
	boolean removeGroup(Group group);
	
	/**
	 * Returns this user's current calculated permissions
	 * 
	 * @return a collection of all applicable permissions
	 */
	default Collection<String> getEffectivePermissions() {
		return getEffectivePermissions(null);
	}
	
	/**
	 * Returns this user's current calculated permissions with optional world
	 * 
	 * @param world the world
	 * @return a collection of all applicable permissions
	 */
	Collection<String> getEffectivePermissions(@Nullable String world);
	
	/**
	 * Gets whether this user has a specific permission
	 * 
	 * @param permission the permission
	 * @return true if and only if the user has the permission
	 */
	default boolean hasPermission(String permission) {
		return hasPermission(permission, null);
	}
	
	/**
	 * Gets whether this user has a specific permission with optional world
	 * 
	 * @param permission the permission
	 * @param world the world
	 * @return true if and only if the user has the permission
	 */
	default boolean hasPermission(String permission, @Nullable String world) {
		return getEffectivePermissions(world).contains(permission);
	}
	
	/**
	 * Instructs this user to recalculate its main permissions
	 * 
	 */
	default void recalculate() {
		recalculate(null);
	}
	
	/**
	 * Instructs this user to recalculate its permissions with optional world
	 * 
	 * @param world the world
	 */
	void recalculate(@Nullable String world);
	
	/**
	 * Whether this group is equal to another. Uses {@link #getId()}
	 * 
	 * @param group the other group
	 * @return true if and only if the IDs are equal according to Object#equals
	 */
	default boolean equals(User user) {
		return user.getId().equals(getId());
	}
	
}
