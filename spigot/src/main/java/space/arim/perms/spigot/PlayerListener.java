/* 
 * ArimPerms-spigot
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * ArimPerms-spigot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ArimPerms-spigot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ArimPerms-spigot. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.perms.spigot;

import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import space.arim.perms.api.User;

class PlayerListener implements Listener {

	private final ArimPermsSpigot plugin;
	
	PlayerListener(ArimPermsSpigot plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void onPreLogin(AsyncPlayerPreLoginEvent evt) {
		User user = plugin.core.getUserByUUID(evt.getUniqueId());
		user.recalculate();
		plugin.getServer().getWorlds().forEach((world) -> user.recalculate(world.getName()));
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void onLogin(PlayerLoginEvent evt) {
		try {
			plugin.getInjector(evt.getPlayer()).inject(plugin.getInjection(evt.getPlayer()));
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
			plugin.getLogger().log(Level.WARNING, "Could not inject permissible for " + evt.getPlayer().getName() + "!", ex);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	private void onQuit(PlayerQuitEvent evt) {
		try {
			plugin.getInjector(evt.getPlayer()).uninject();
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException ex) {
			plugin.getLogger().log(Level.WARNING, "Could not uninject permissible for " + evt.getPlayer().getName() + "!", ex);
		}
	}
	
}
