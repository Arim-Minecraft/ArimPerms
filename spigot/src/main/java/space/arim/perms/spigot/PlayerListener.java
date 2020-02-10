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
import org.bukkit.event.player.PlayerTeleportEvent;

class PlayerListener implements Listener {

	private final ArimPermsSpigot plugin;
	
	PlayerListener(ArimPermsSpigot plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void onPreLogin(AsyncPlayerPreLoginEvent evt) {
		plugin.core.getUserByUUID(evt.getUniqueId()).recalculate();
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void onLogin(PlayerLoginEvent evt) {
		plugin.core.getUserByUUID(evt.getPlayer().getUniqueId()).recalculate(evt.getPlayer().getWorld().getName());
		try {
			plugin.getInjector(evt.getPlayer()).inject(plugin.getInjection(evt.getPlayer()));
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
			plugin.getLogger().log(Level.WARNING, "Could not inject permissible for " + evt.getPlayer().getName() + "!", ex);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void onWorldChange(PlayerTeleportEvent evt) {
		if (!evt.getFrom().getWorld().getName().equals(evt.getTo().getWorld().getName())) {
			plugin.core.getUserByUUID(evt.getPlayer().getUniqueId()).recalculate(evt.getTo().getWorld().getName());
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
