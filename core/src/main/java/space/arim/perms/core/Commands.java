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
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
				} else if (args[0].equalsIgnoreCase("save-all")) {
					
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
	
	private void userCommand(CmdSender sender, String[] args, boolean async) {
		String userId = getIdForName(args[1], async);
		if (userId != null) {
			if (args.length > 2) {
				User user = core.users().getUser(userId);
				if (args[2].equalsIgnoreCase("add")) {
					if (args.length > 3) {
						Group[] groups = validateGroups(sender, args[3]);
						if (groups != null) {
							sendMessage(sender, core.messages().getString("cmds.user.manipulate.add." + (recalcUserGroupsIf(user, CollectionsUtil.checkForAnyMatches(groups, user::addGroup)) ? "done" : "already")).replace("%USER%", args[1]).replace("%LIST%", args[3]));
						}
					} else {
						sendMessage(sender, core.messages().getString("cmds.user.manipulate.add.usage").replace("%USER%", args[1]));
					}
				} else if (args[2].equalsIgnoreCase("remove")) {
					if (args.length > 3) {
						Group[] groups = validateGroups(sender, args[3]);
						if (groups != null) {
							sendMessage(sender, core.messages().getString("cmds.user.manipulate.remove." + (recalcUserGroupsIf(user, CollectionsUtil.checkForAnyMatches(groups, user::removeGroup)) ? "done" : "already")).replace("%USER%", args[1]).replace("%LIST%", args[3]));
						}
					} else {
						sendMessage(sender, core.messages().getString("cmds.user.manipulate.remove.usage").replace("%USER%", args[1]));
					}
				} else if (args[3].equalsIgnoreCase("clear")) {
					sendMessage(sender, core.messages().getString("cmds.user.manipulate.clear." + (recalcUserGroupsIf(user, CollectionsUtil.checkForAnyMatches(user.getGroups(), user::removeGroup)) ? "done" : "already")).replace("%USER%", args[1]));
				} else if (args[3].equalsIgnoreCase("recalc")) {
					recalcUserPerms(user);
					sendMessage(sender, core.messages().getString("cmds.user.update.recalc").replace("%USER%", args[1]));
				} else if (args[3].equalsIgnoreCase("list")) {
					Group[] groups = user.getGroups();
					sendMessage(sender, core.messages().getString("cmds.user.info.list." + (groups.length > 0 ? "list" : "none")).replace("%USER%", args[1]).replace("%LIST%", StringsUtil.concat(CollectionsUtil.convertAll(groups, (group) -> group.getId()), ',')));
				} else if (args[3].equalsIgnoreCase("list-perms")) {
					String category = (args.length > 3) ? args[3] : null;
					String categoryMsg = (category == null) ? core.messages().getString("cmds.category.general") : core.messages().getString("cmds.category.for-specific").replace("%CATEGORY%", category);
					Collection<String> perms = user.getEffectivePermissions(category);
					sendMessage(sender, core.messages().getString("cmds.user.info.list-perms." + (!perms.isEmpty() ? "list" : "none")).replace("%USER%", args[1]).replace("%CATEGORY%", categoryMsg).replace("%LIST%", StringsUtil.concat(perms, ',')));
				} else {
					sendMessage(sender, core.messages().getString("cmds.user.usage").replace("%TARGET%", args[1]));
				}
			} else {
				sendMessage(sender, core.messages().getString("cmds.user.usage").replace("%TARGET%", args[1]));
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
					String category = (args.length > 4) ? args[4] : null;
					String categoryMsg = (category == null) ? core.messages().getString("cmds.category.general") : core.messages().getString("cmds.category.for-specific").replace("%CATEGORY%", category);
					sendMessage(sender, core.messages().getString("cmds.group.manipulate.add." + (recalcGroupPermsIf(group, group.addPermissions(category, Arrays.asList(args[3].split(",")))) ? "done" : "already")).replace("%CATEGORY%", categoryMsg).replace("%GROUP%", args[1]).replace("%LIST%", args[3]));
				} else {
					sendMessage(sender, core.messages().getString("cmds.group.manipulate.add.usage").replace("%GROUP%", args[1]));
				}
			} else if (args[2].equalsIgnoreCase("remove")) {
				if (args.length > 4) {
					String category = (args.length > 4) ? args[4] : null;
					String categoryMsg = (category == null) ? core.messages().getString("cmds.category.general") : core.messages().getString("cmds.category.for-specific").replace("%CATEGORY%", category);
					sendMessage(sender, core.messages().getString("cmds.group.manipulate.remove." + (recalcGroupPermsIf(group, group.removePermissions(category, Arrays.asList(args[3].split(",")))) ? "done" : "already")).replace("%CATEGORY%", categoryMsg).replace("%GROUP%", args[1]).replace("%LIST%", args[3]));
				} else {
					sendMessage(sender, core.messages().getString("cmds.group.manipulate.remove.usage").replace("%GROUP%", args[1]));
				}
			} else if (args[2].equalsIgnoreCase("clear")) {
				String category = (args.length > 3) ? args[4] : null;
				String categoryMsg = (category == null) ? core.messages().getString("cmds.category.general") : core.messages().getString("cmds.category.for-specific").replace("%CATEGORY%", category);
				sendMessage(sender, core.messages().getString("cmds.group.manipulate.clear." + (recalcGroupPermsIf(group, group.clearPermissions(category)) ? "done" : "already")).replace("%CATEGORY%", categoryMsg).replace("%GROUP%", args[1]));
			} else if (args[2].equalsIgnoreCase("add-parent")) {
				Group[] parents = validateGroups(sender, args[3]);
				if (parents != null) {
					sendMessage(sender, core.messages().getString("cmds.group.manipulate.add-parent." + (recalcGroupParentsIf(group, CollectionsUtil.checkForAnyMatches(parents, group::addParent)) ? "done" : "already")).replace("%GROUP%", args[1]).replace("%LIST%", args[3]));
				}
			} else if (args[2].equalsIgnoreCase("remove-parent")) {
				Group[] parents = validateGroups(sender, args[3]);
				if (parents != null) {
					sendMessage(sender, core.messages().getString("cmds.group.manipulate.remove-parent." + (recalcGroupParentsIf(group, CollectionsUtil.checkForAnyMatches(parents, group::removeParent)) ? "done" : "already")).replace("%GROUP%", args[1]).replace("%LIST%", args[3]));
				}
			} else if (args[2].equalsIgnoreCase("clear-parents")) {
				sendMessage(sender, core.messages().getString("cmds.group.manipulate.clear-parents." + (recalcGroupParentsIf(group, CollectionsUtil.checkForAnyMatches(group.getEffectiveParents(), group::removeParent)) ? "done" : "already")).replace("%GROUP%", args[1]));
			} else if (args[3].equalsIgnoreCase("recalc-perms")) {
				recalcUserPermsForGroup(group);
				sendMessage(sender, core.messages().getString("cmds.group.update.recalc-perms").replace("%GROUP%", args[1]));
			} else if (args[3].equalsIgnoreCase("recalc-parents")) {
				recalcGroupParents(group);
				sendMessage(sender, core.messages().getString("cmds.group.update.recalc-parents").replace("%GROUP%", args[1]));
			} else if (args[3].equalsIgnoreCase("create")) {
				sendMessage(sender, core.messages().getString("cmds.group.create.already").replace("%GROUP%", args[1]));
			} else if (args[3].equalsIgnoreCase("list")) {
				String category = (args.length > 3) ? args[3] : null;
				String categoryMsg = (category == null) ? core.messages().getString("cmds.category.general") : core.messages().getString("cmds.category.for-specific").replace("%CATEGORY%", category);
				Collection<String> perms = group.getPermissions(category);
				sendMessage(sender, core.messages().getString("cmds.group.info.list." + (!perms.isEmpty() ? "list" : "none")).replace("%GROUP%", args[1]).replace("%CATEGORY%", categoryMsg).replace("%LIST%", StringsUtil.concat(perms, ',')));
			} else if (args[3].equalsIgnoreCase("list-users")) {
				Collection<String> users = getUsersInGroup(group).map((user) -> user.getId()).collect(Collectors.toSet());
				sendMessage(sender, core.messages().getString("cmds.user.info.list-users." + (!users.isEmpty() ? "list" : "none")).replace("%GROUP%", args[1]).replace("%LIST%", StringsUtil.concat(users, ',')));
			} else if (args[3].equalsIgnoreCase("list-parents")) {
				Group[] parents = group.getParents();
				sendMessage(sender, core.messages().getString("cmds.user.info.list-parents." + (parents.length > 0 ? "list" : "none")).replace("%GROUP%", args[1]).replace("%LIST%", StringsUtil.concat(CollectionsUtil.convertAll(parents, (parent) -> parent.getId()), ',')));
			} else {
				sendMessage(sender, core.messages().getString("cmds.group.usage").replace("%GROUP%", args[1]));
			}
		} else {
			sendMessage(sender, core.messages().getString("cmds.group.usage").replace("%GROUP%", args[1]));
		}
	}
	
	private void noPermission(CmdSender sender) {
		sendMessage(sender, core.messages().getString("cmds.no-permission"));
	}
	
	private void sendMessage(CmdSender sender, String message) {
		sender.sendMessage(core.messages().getBoolean("prefix.enable") ? core.messages().getString("prefix.value") + message : message);
	}
	
	private Stream<User> getUsersInGroup(Group group) {
		return core.users().getUsers().stream().filter((user) -> CollectionsUtil.checkForAnyMatches(user.getGroups(), group::equals));
	}
	
	private void recalcUserPerms(User user) {
		HashSet<String> categorys = new HashSet<String>();
		for (Group group : user.getGroups()) {
			group.getCategories().forEach(categorys::add);
		}
		categorys.forEach(user::recalculate);
	}
	
	private void recalcUserPermsForGroup(Group group) {
		getUsersInGroup(group).forEach((user) -> group.getCategories().forEach(user::recalculate));
	}
	
	private void recalcGroupParents(Group group) {
		group.recalculate();
		recalcUserPermsForGroup(group);
	}
	
	private boolean recalcUserGroupsIf(User user, boolean changed) {
		if (changed) {
			recalcUserPerms(user);
		}
		return changed;
	}
	
	private boolean recalcGroupPermsIf(Group group, boolean changed) {
		if (changed) {
			recalcUserPermsForGroup(group);
		}
		return changed;
	}
	
	private boolean recalcGroupParentsIf(Group group, boolean changed) {
		if (changed) {
			recalcGroupParents(group);
		}
		return changed;
	}

}
