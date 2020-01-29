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

import java.util.Arrays;

import space.arim.universal.util.collections.CollectionsUtil;

import space.arim.api.concurrent.AsyncExecution;
import space.arim.api.uuid.PlayerNotFoundException;

import space.arim.perms.api.ArimPerms;
import space.arim.perms.api.CmdSender;
import space.arim.perms.api.CommandManager;
import space.arim.perms.api.Group;
import space.arim.perms.api.User;

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
			if (args.length > 3) {
				User user = core.users().getUser(userId);
				if (args[2].equalsIgnoreCase("add")) {
					Group[] groups = validateGroups(sender, args[3]);
					if (groups != null) {
						sendMessage(sender, core.messages().getString("cmds.user.manipulate.add." + (CollectionsUtil.checkForAnyMatches(groups, user::addGroup) ? "done" : "already")));
					}
				} else if (args[2].equalsIgnoreCase("remove")) {
					Group[] groups = validateGroups(sender, args[3]);
					if (groups != null) {
						sendMessage(sender, core.messages().getString("cmds.user.manipulate.remove." + (CollectionsUtil.checkForAnyMatches(groups, user::removeGroup) ? "done" : "already")));
					}
				} else {
					sendMessage(sender, core.messages().getString("cmds.user.manipulate.usage").replace("%TARGET%", args[1]));
				}
			} else {
				sendMessage(sender, core.messages().getString("cmds.user.manipulate.usage").replace("%TARGET%", args[1]));
			}
		} else {
			sendMessage(sender, core.messages().getString("cmds.user.not-found").replace("%TARGET%", args[1]));
		}
	}
	
	private Group[] validateGroups(CmdSender sender, String input) {
		String[] parentsList = input.split(",");
		Group[] parents = new Group[parentsList.length];
		for (int n = 0; n < parentsList.length; n++) {
			if (core.groups().getPossibleGroup(parentsList[n]) == null) {
				sendMessage(sender, core.messages().getString("cmds.group.not-found").replace("%GROUP%", parentsList[n]));
				return null;
			}
			parents[n] = core.groups().getGroup(parentsList[n]);
		}
		return parents;
	}
	
	private void groupCommand(CmdSender sender, String[] args) {
		Group group = core.groups().getPossibleGroup(args[1]);
		if (group != null) {
			if (args.length > 3) {
				if (args[2].equalsIgnoreCase("add")) {
					sendMessage(sender, core.messages().getString("cmds.group.manipulate.add." + (group.addPermissions((args.length > 4) ? args[5] : null, Arrays.asList(args[3].split(","))) ? "done" : "already")).replace("%GROUP%", args[1]).replace("%LIST%", args[3]));
				} else if (args[2].equalsIgnoreCase("remove")) {
					sendMessage(sender, core.messages().getString("cmds.group.manipulate.remove." + (group.removePermissions((args.length > 4) ? args[5] : null, Arrays.asList(args[3].split(","))) ? "done" : "already")).replace("%GROUP%", args[1]).replace("%LIST%", args[3]));
				} else if (args[2].equalsIgnoreCase("clear")) {
					// TODO add clear permissions
				} else if (args[2].equalsIgnoreCase("addparent")) {
					Group[] parents = validateGroups(sender, args[3]);
					if (parents != null) {
						sendMessage(sender, core.messages().getString("cmds.group.manipulate.add-parent." + (CollectionsUtil.checkForAnyMatches(parents, group::addParent) ? "done" : "already")).replace("%GROUP%", args[1]).replace("%LIST%", args[3]));
					}
				} else if (args[2].equalsIgnoreCase("removeparent")) {
					Group[] parents = validateGroups(sender, args[3]);
					if (parents != null) {
						sendMessage(sender, core.messages().getString("cmds.group.manipulate.remove-parent." + (CollectionsUtil.checkForAnyMatches(parents, group::removeParent) ? "done" : "already")).replace("%GROUP%", args[1]).replace("%LIST%", args[3]));
					}
				} else if (args[2].equalsIgnoreCase("clearparents")) {
					// TODO add clear parents
				} else {
					// TODO send usage message here
				}
			} else {
				sendMessage(sender, core.messages().getString("cmds.group.manipulate.usage").replace("%GROUP%", args[1]));
			}
		} else if (args.length > 2 && args[2].equalsIgnoreCase("create")) {
			if (args.length > 3) {
				Group[] parents = validateGroups(sender, args[3]);
				if (parents != null) {
					group = core.groups().getGroup(args[1]);
					for (Group parent : parents) {
						group.addParent(parent);
					}
					sendMessage(sender, core.messages().getString("cmds.group.created.with-parents").replace("%GROUP%", args[1]).replace("%LIST%", args[3]));
					return;
				}
			}
			group = core.groups().getGroup(args[1]);
			sendMessage(sender, core.messages().getString("cmds.group.created.done").replace("%GROUP%", args[1]));
		} else {
			sendMessage(sender, core.messages().getString("cmds.group.not-found").replace("%GROUP%", args[1]));
		}
	}
	
	private void sendMessage(CmdSender sender, String message) {
		sender.sendMessage(core.messages().getBoolean("prefix.enable") ? core.messages().getString("prefix.value") + message : message);
	}

}
