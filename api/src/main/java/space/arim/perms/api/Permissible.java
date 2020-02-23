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

import org.eclipse.jdt.annotation.Nullable;

/**
 * Anything which may hold permissions. <br>
 * <br>
 * <b>Specifications</b>: <br>
 * * {@link #getId()} must return a unique group ID. <br>
 * * <code>#equals(Object object)</code> <b>MUST</b> be overriden <b>AND</b> check for equivalency using <code>#getId()</code> <br>
 * * <code>{@link #hashCode()}</code> should likewise be overriden and implemented based on <code>#getId()</code> 
 * 
 * @author A248
 *
 */
public interface Permissible {

	/**
	 * Whether the permission holder has a specific permission, in the general category.
	 * 
	 * @param permission the permission
	 * @return true if and only if the permission holder has the permission
	 */
	default boolean hasPermission(String permission) {
		return hasPermission(permission, null);
	}
	
	/**
	 * Whether the permission holder has a specific permission, in the specific category. <br>
	 * <br>
	 * <b>Implementation note</b>: <br>
	 * Should check whether the permission holder has the permission ONLY for the category provided.
	 * It should NOT determine whether the user has permission for the category or the general category.
	 * 
	 * @param permission the permission
	 * @param category the category
	 * @return true if and only if the permission holder has the permission
	 */
	boolean hasPermission(String permission, @Nullable String category);
	
	/**
	 * Gets the ID of this Permissible. Should be unique
	 * 
	 * @return the String based id
	 */
	String getId();
	
}
