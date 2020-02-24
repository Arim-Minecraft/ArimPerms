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

import space.arim.universal.registry.Registry;
import space.arim.universal.registry.RequireRegistration;

import space.arim.api.concurrent.AsyncExecution;
import space.arim.api.concurrent.SyncExecution;
import space.arim.api.uuid.UUIDResolver;

/**
 * A more specific subclass of {@link PermissionsPlugin} intended to implement the ArimPerms API.
 * 
 * @author A248
 *
 */
public interface ArimPermsApi extends PermissionsPlugin, Configurable {

	/**
	 * The corresponding {@link Registry} instance used for resource registration. Use {@link Registry#getEvents()} for events firing.
	 * 
	 * @return the corresponding Registry
	 */
	@RequireRegistration({AsyncExecution.class, SyncExecution.class, UUIDResolver.class})
	Registry getRegistry();
	
	/**
	 * Gets the {@link User} for a player's UUID.
	 * 
	 * @param uuid the player uuid
	 * @return the corresponding user
	 */
	default User getUserByUUID(UUID uuid) {
		return users().getUser(uuid.toString().replace("-", ""));
	}
	
	@Override
	default boolean hasPermission(UUID player, String permission, @Nullable String section) {
		return getUserByUUID(player).hasPermission(permission, section);
	}
	
	/**
	 * Allows for handling groups
	 * 
	 * @return the GroupManager
	 */
	GroupManager groups();
	
	/**
	 * Allows for handling users
	 * 
	 * @return the UserManager
	 */
	UserManager users();
	
}
