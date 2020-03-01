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

import space.arim.api.platform.bungee.BungeeMessages;

import space.arim.perms.api.CmdSender;

public class WrappedSender implements CmdSender {

	private final CommandSender sender;
	
	WrappedSender(CommandSender sender) {
		this.sender = sender;
	}
	
	@Override
	public boolean hasPermission(String permission) {
		return sender.hasPermission(permission);
	}
	
	@Override
	public void sendMessage(String message) {
		sender.sendMessage(BungeeMessages.get().colour(message));
	}
	
}
