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

import java.io.File;
import java.util.UUID;

import org.eclipse.jdt.annotation.Nullable;

import space.arim.universal.registry.UniversalRegistry;

import space.arim.api.config.SimpleConfig;

public interface ArimPerms extends PermissionsPlugin, Configurable {

	default UniversalRegistry getRegistry() {
		return UniversalRegistry.get();
	}
	
	@Override
	default boolean userHasPermission(UUID player, String permission, @Nullable String world) {
		return users().getUser(player.toString().replace("-", "")).hasPermission(permission, world);
	}
	
	@Override
	default boolean groupHasPermission(String group, String permission, @Nullable String world) {
		return groups().getGroup(group).hasPermission(permission, world);
	}
	
	File getFolder();
	
	GroupManager groups();
	
	UserManager users();
	
	LogManager logs();
	
	DataManager data();
	
	SimpleConfig config();
	
	@Override
	default void reload(boolean first) {
		config().reload();
		logs().reload(first);
		data().reload(first);
		data().readyDb();
		groups().reload(first);
		users().reload(first);
		data().closeDb();
	}
	
	@Override
	default void close() {
		data().readyDb();
		users().close();
		groups().close();
		data().closeDb();
		data().close();
		logs().close();
	}
	
}
