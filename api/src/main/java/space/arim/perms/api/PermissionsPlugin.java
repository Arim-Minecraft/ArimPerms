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

import java.util.UUID;

import org.eclipse.jdt.annotation.Nullable;

/**
 * A generic permissions plugin.
 * 
 * @author A248
 *
 */
public interface PermissionsPlugin {

	/**
	 * Checks whether a player has a specific permission with optional permissions category. <br>
	 * On Spigot servers, the categories are usually the worlds.
	 * On BungeeCord proxies, the categories are usually the subservers.
	 * 
	 * @param player the player UUID
	 * @param permission the permission
	 * @param category the permissions category, if <code>null</code>, the main category
	 * @return true if the user has the permission, false otherwise
	 */
	boolean hasPermission(UUID player, String permission, @Nullable String category);
	
}
