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
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

/**
 * A permissions user. <br>
 * <br>
 * <b>Specifications</b>: <br>
 * * {@link #getId()} must return a unique user ID. <br>
 * * <code>#equals(Object object)</code> <b>MUST</b> be overriden <b>AND</b> check for equivalency using <code>#getId()</code> <br>
 * * <code>{@link Object#hashCode()}</code> should likewise be overriden and implemented based on <code>#getId()</code> <br>
 * * {@link #hasPermission(String, String)} should check whether the user has the permission ONLY for the category provided.
 * It should NOT determine whether the user has permission for the category or the general category.
 * 
 * @author A248
 *
 */
public interface User extends Permissible {

	/**
	 * Gets the ID of this user. Should be unique
	 * 
	 * @return the String based id
	 */
	@Override
	String getId();
	
	/**
	 * Gets a backed set of groups from which this user inherits.
	 * 
	 * @return an immutable <i>view</i> of all applicable groups
	 */
	Set<Group> getGroups();
	
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
	 * Returns this user's current calculated permissions with optional category
	 * 
	 * @param category the category
	 * @return a collection of all applicable permissions
	 */
	Collection<String> getEffectivePermissions(@Nullable String category);
	
	/**
	 * Gets whether this user has a specific permission
	 * 
	 * @param permission the permission
	 * @return true if and only if the user has the permission
	 */
	@Override
	default boolean hasPermission(String permission) {
		return hasPermission(permission, null);
	}
	
	/**
	 * Gets whether this user has a specific permission with optional category
	 * 
	 * @param permission the permission
	 * @param category the category
	 * @return true if and only if the user has the permission
	 */
	@Override
	default boolean hasPermission(String permission, @Nullable String category) {
		return getEffectivePermissions(category).contains(permission);
	}
	
	/**
	 * Instructs this user to recalculate its general permissions
	 * 
	 */
	default void recalculate() {
		recalculate(null);
	}
	
	/**
	 * Instructs this user to recalculate its permissions with optional category
	 * 
	 * @param category the category
	 */
	void recalculate(@Nullable String category);
	
}
