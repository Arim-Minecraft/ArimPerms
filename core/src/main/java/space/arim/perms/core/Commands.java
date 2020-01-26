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
package space.arim.perms.core;

import space.arim.api.concurrent.AsyncExecution;
import space.arim.api.uuid.PlayerNotFoundException;

import space.arim.perms.api.ArimPerms;
import space.arim.perms.api.CmdSender;
import space.arim.perms.api.CommandManager;
import space.arim.perms.api.Group;

public class Commands implements CommandManager {

	private final ArimPerms core;
	
	public Commands(ArimPerms core) {
		this.core = core;
	}
	
	@Override
	public void runCommand(CmdSender sender, String[] args) {
		if (sender.hasPermission("arimperms.cmd.use")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("user")) {
					if (args.length > 1) {
						if (core.isOnlineMode()) {
							core.getRegistry().getRegistration(AsyncExecution.class).execute(() -> userCommand(sender, args));
						} else {
							userCommand(sender, args);
						}
					} else {
						sendMessage(sender, core.messages().getString("cmds.user.find"));
					}
				} else if (args[0].equalsIgnoreCase("group")) {
					if (args.length > 1) {
						groupCommand(sender, args);
					} else {
						sendMessage(sender, core.messages().getString("cmds.group.find"));
					}
				} else {
					sendMessage(sender, core.messages().getString("cmds.base-usage"));
				}
			} else {
				sendMessage(sender, core.messages().getString("cmds.base-usage"));
			}
		} else {
			sendMessage(sender, core.messages().getString("cmds.no-permission"));
		}
	}
	
	private String getIdForName(String name) {
		try {
			return core.resolver().resolveName(name, core.isOnlineMode()).toString().replace("-", "");
		} catch (PlayerNotFoundException ex) {
			return null;
		}
	}
	
	private void userCommand(CmdSender sender, String[] args) {
		String userId = getIdForName(args[1]);
		if (userId != null) {
			if (args.length > 2) {
				
			} else {
				sendMessage(sender, core.messages().getString("cmds.user.manipulate.usage").replace("%TARGET%", args[1]));
			}
		} else {
			sendMessage(sender, core.messages().getString("cmds.user.not-found").replace("%TARGET%", args[1]));
		}
	}
	
	private void groupCommand(CmdSender sender, String[] args) {
		Group group = core.groups().getPossibleGroup(args[1]);
		if (group != null) {
			if (args.length > 2) {
				
			} else {
				sendMessage(sender, core.messages().getString("cmds.group.manipulate.usage").replace("%GROUP%", args[1]));
			}
		} else {
			sendMessage(sender, core.messages().getString("cmds.group.not-found").replace("%GROUP%", args[1]));
		}
	}
	
	private void sendMessage(CmdSender sender, String message) {
		sender.sendMessage(core.messages().getBoolean("prefix.enable") ? core.messages().getString("prefix.value") + message : message);
	}

}
