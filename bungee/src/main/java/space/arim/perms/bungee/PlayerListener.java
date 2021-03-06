/* 
 * ArimPerms-bungee
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * ArimPerms-bungee is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ArimPerms-bungee is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ArimPerms-bungee. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.perms.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import space.arim.perms.api.User;

class PlayerListener implements Listener {

	private final ArimPermsBungee plugin;
	
	PlayerListener(ArimPermsBungee plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = Byte.MIN_VALUE)
	private void onLogin(LoginEvent evt) {
		User user = plugin.core.getUserByUUID(evt.getConnection().getUniqueId());
		user.recalculate();
		plugin.getProxy().getServers().keySet().forEach(user::recalculate);
	}
	
	@EventHandler(priority = Byte.MIN_VALUE)
	private void onPermsCheck(PermissionCheckEvent evt) {
		CommandSender sender = evt.getSender();
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer player = ((ProxiedPlayer) sender);
			User user = plugin.core.getUserByUUID(player.getUniqueId());
			evt.setHasPermission(user.hasPermission(evt.getPermission()) || user.hasPermission(evt.getPermission(), player.getServer().getInfo().getName()));
		}
	}
	
}
