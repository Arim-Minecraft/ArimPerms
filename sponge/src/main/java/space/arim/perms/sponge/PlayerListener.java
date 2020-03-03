/* 
 * ArimPerms-sponge
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * ArimPerms-sponge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ArimPerms-sponge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ArimPerms-sponge. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.perms.sponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import space.arim.perms.api.User;

class PlayerListener {

	private final ArimPermsSponge plugin;
	
	PlayerListener(ArimPermsSponge plugin) {
		this.plugin = plugin;
	}
	
	@Listener(order = Order.PRE)
	public void onPreLogin(ClientConnectionEvent.Auth evt) {
		User user = plugin.core.getUserByUUID(evt.getProfile().getUniqueId());
		user.recalculate();
		Sponge.getGame().getServer().getAllWorldProperties().forEach((wp) -> user.recalculate(wp.getWorldName()));
	}
	
}
