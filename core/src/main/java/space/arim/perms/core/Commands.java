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
import java.util.Collection;
import java.util.stream.Collectors;

import space.arim.universal.util.collections.CollectionsUtil;

import space.arim.api.concurrent.AsyncExecution;
import space.arim.api.util.StringsUtil;
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
	
	/*
	 * Permissions
	 * 
	 * arimperms.use
	 * 
	 * arimperms.user.manipulate.add
	 * arimperms.user.manipulate.remove
	 * arimperms.user.manipulate.clear
	 * arimperms.user.info.list
	 * arimperms.user.info.listperms
	 * 
	 * arimperms.group.create
	 * arimperms.group.manipulate.add
	 * arimperms.group.manipulate.remove
	 * arimperms.group.manipulate.clear
	 * arimperms.group.manipulate.addparent
	 * arimperms.group.manipulate.removeparent
	 * arimperms.group.manipulate.clearparents
	 * arimperms.group.info.list
	 * arimperms.group.info.listusers
	 * arimperms.group.info.listparents
	 * 
	 * TODO
	 * 
	 * Add permission checks
	 * 
	 */
	
	@Override
	public void runCommand(CmdSender sender, String[] args) {
		if (sender.hasPermission("arimperms.use")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("user")) {
					if (args.length > 1) {
						if (core.isOnlineMode()) {
							core.getRegistry().getRegistration(AsyncExecution.class).execute(() -> userCommand(sender, args, true));
						} else {
							userCommand(sender, args, false);
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
			noPermission(sender);
		}
	}
	
	private String getIdForName(String name, boolean query) {
		try {
			return core.resolver().resolveName(name, query).toString().replace("-", "");
		} catch (PlayerNotFoundException ex) {
			return null;
		}
	}
	
	private void userCommand(CmdSender sender, String[] args, boolean queryName) {
		String userId = getIdForName(args[1], queryName);
		if (userId != null) {
			if (args.length > 2) {
				User user = core.users().getUser(userId);
				if (args[2].equalsIgnoreCase("add")) {
					if (args.length > 3) {
						Group[] groups = validateGroups(sender, args[3]);
						if (groups != null) {
							sendMessage(sender, core.messages().getString("cmds.user.manipulate.add." + (CollectionsUtil.checkForAnyMatches(groups, user::addGroup) ? "done" : "already")).replace("%USER%", args[1]).replace("%LIST%", args[3]));
						}
					} else {
						sendMessage(sender, core.messages().getString("cmds.user.manipulate.add.usage").replace("%USER%", args[1]));
					}
				} else if (args[2].equalsIgnoreCase("remove")) {
					if (args.length > 3) {
						Group[] groups = validateGroups(sender, args[3]);
						if (groups != null) {
							sendMessage(sender, core.messages().getString("cmds.user.manipulate.remove." + (CollectionsUtil.checkForAnyMatches(groups, user::removeGroup) ? "done" : "already")).replace("%USER%", args[1]).replace("%LIST%", args[3]));
						}
					} else {
						sendMessage(sender, core.messages().getString("cmds.user.manipulate.remove.usage").replace("%USER%", args[1]));
					}
				} else if (args[3].equalsIgnoreCase("clear")) {
					sendMessage(sender, core.messages().getString("cmds.user.manipulate.clear." + (CollectionsUtil.checkForAnyMatches(user.getGroups(), user::removeGroup) ? "done" : "already")).replace("%USER%", args[1]));
				} else if (args[3].equalsIgnoreCase("list")) {
					Group[] groups = user.getGroups();
					sendMessage(sender, core.messages().getString("cmds.user.info.list." + (groups.length > 0 ? "list" : "none")).replace("%USER%", args[1]).replace("%LIST%", StringsUtil.concat(CollectionsUtil.convertAll(groups, (group) -> group.getId()), ',')));
				} else if (args[3].equalsIgnoreCase("listperms")) {
					String world = (args.length > 3) ? args[3] : null;
					String worldMsg = (world == null) ? "" : core.messages().getString("cmds.for-world").replace("%WORLD%", world);
					Collection<String> perms = user.getEffectivePermissions(world);
					sendMessage(sender, core.messages().getString("cmds.user.info.listperms." + (!perms.isEmpty() ? "list" : "none")).replace("%USER%", args[1]).replace("%WORLD%", worldMsg).replace("%LIST%", StringsUtil.concat(perms, ',')));
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
			existingGroupCommand(sender, group, args);
		} else if (args.length > 2 && args[2].equalsIgnoreCase("create")) {
			if (args.length > 3) {
				Group[] parents = validateGroups(sender, args[3]);
				if (parents != null) {
					group = core.groups().getGroup(args[1]);
					for (Group parent : parents) {
						group.addParent(parent);
					}
					sendMessage(sender, core.messages().getString("cmds.group.create.with-parents").replace("%GROUP%", args[1]).replace("%LIST%", args[3]));
					return;
				}
			}
			core.groups().getGroup(args[1]);
			sendMessage(sender, core.messages().getString("cmds.group.create.done").replace("%GROUP%", args[1]));
		} else {
			sendMessage(sender, core.messages().getString("cmds.group.not-found").replace("%GROUP%", args[1]));
		}
	}
	
	private void existingGroupCommand(CmdSender sender, Group group, String[] args) {
		if (args.length > 2) {
			if (args[2].equalsIgnoreCase("add")) {
				if (args.length > 3) {
					if (args[3].contains(";") || args[3].contains(":")) {
						sendMessage(sender, core.messages().getString("cmds.group.manipulate.add.invalid-char"));
					}
					String world = (args.length > 4) ? args[4] : null;
					String worldMsg = (world == null) ? "" : core.messages().getString("cmds.for-world").replace("%WORLD%", world);
					sendMessage(sender, core.messages().getString("cmds.group.manipulate.add." + (group.addPermissions(world, Arrays.asList(args[3].split(","))) ? "done" : "already")).replace("%WORLD%", worldMsg).replace("%GROUP%", args[1]).replace("%LIST%", args[3]));
				} else {
					sendMessage(sender, core.messages().getString("cmds.group.manipulate.add.usage").replace("%GROUP%", args[1]));
				}
			} else if (args[2].equalsIgnoreCase("remove")) {
				if (args.length > 4) {
					String world = (args.length > 4) ? args[4] : null;
					String worldMsg = (world == null) ? "" : core.messages().getString("cmds.for-world").replace("%WORLD%", world);
					sendMessage(sender, core.messages().getString("cmds.group.manipulate.remove." + (group.removePermissions(world, Arrays.asList(args[3].split(","))) ? "done" : "already")).replace("%WORLD%", worldMsg).replace("%GROUP%", args[1]).replace("%LIST%", args[3]));
				} else {
					sendMessage(sender, core.messages().getString("cmds.group.manipulate.remove.usage").replace("%GROUP%", args[1]));
				}
			} else if (args[2].equalsIgnoreCase("clear")) {
				String world = (args.length > 3) ? args[4] : null;
				String worldMsg = (world == null) ? "" : core.messages().getString("cmds.for-world").replace("%WORLD%", world);
				sendMessage(sender, core.messages().getString("cmds.group.manipulate.clear." + (world == null ? CollectionsUtil.checkForAnyMatches(group.getWorlds(), group::clearPermissions) : group.clearPermissions(world) ? "done" : "already")).replace("%WORLD%", worldMsg).replace("%GROUP%", args[1]));
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
				sendMessage(sender, core.messages().getString("cmds.group.manipulate.clear-parents." + (CollectionsUtil.checkForAnyMatches(group.getEffectiveParents(), group::removeParent) ? "done" : "already")).replace("%GROUP%", args[1]));
			} else if (args[3].equalsIgnoreCase("create")) {
				sendMessage(sender, core.messages().getString("cmds.group.create.already").replace("%GROUP%", args[1]));
			} else if (args[3].equalsIgnoreCase("list")) {
				String world = (args.length > 3) ? args[3] : null;
				String worldMsg = (world == null) ? "" : core.messages().getString("cmds.for-world").replace("%WORLD%", world);
				Collection<String> perms = group.getPermissions(world);
				sendMessage(sender, core.messages().getString("cmds.group.info.list." + (!perms.isEmpty() ? "list" : "none")).replace("%GROUP%", args[1]).replace("%WORLD%", worldMsg).replace("%LIST%", StringsUtil.concat(perms, ',')));
			} else if (args[3].equalsIgnoreCase("listusers")) {
				Collection<String> users = core.users().getUsers().stream().filter((user) -> CollectionsUtil.checkForAnyMatches(user.getGroups(), group::equals)).map((user) -> user.getId()).collect(Collectors.toSet());
				sendMessage(sender, core.messages().getString("cmds.user.info.listusers." + (!users.isEmpty() ? "list" : "none")).replace("%GROUP%", args[1]).replace("%LIST%", StringsUtil.concat(users, ',')));
			} else if (args[3].equalsIgnoreCase("listparents")) {
				Group[] parents = group.getParents();
				sendMessage(sender, core.messages().getString("cmds.user.info.listparents." + (parents.length > 0 ? "list" : "none")).replace("%GROUP%", args[1]).replace("%LIST%", StringsUtil.concat(CollectionsUtil.convertAll(parents, (parent) -> parent.getId()), ',')));
			} else {
				sendMessage(sender, core.messages().getString("cmds.group.manipulate.usage").replace("%GROUP%", args[1]));
			}
		} else {
			sendMessage(sender, core.messages().getString("cmds.group.manipulate.usage").replace("%GROUP%", args[1]));
		}
	}
	
	private void noPermission(CmdSender sender) {
		sendMessage(sender, core.messages().getString("cmds.no-permission"));
	}
	
	private void sendMessage(CmdSender sender, String message) {
		sender.sendMessage(core.messages().getBoolean("prefix.enable") ? core.messages().getString("prefix.value") + message : message);
	}

}
