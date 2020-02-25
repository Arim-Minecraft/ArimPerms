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

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

import space.arim.universal.util.collections.CollectionsUtil;

import space.arim.api.util.CallerFinder;
import space.arim.api.util.CallerFinderProvider;

import space.arim.perms.api.ArimPerms;
import space.arim.perms.api.Group;
import space.arim.perms.api.User;

import net.milkbowl.vault.permission.Permission;

@SuppressWarnings("deprecation")
public class VaultHook extends Permission {

	private final ArimPerms core;
	
	private final ConcurrentHashMap<String, PermissionAttachment> transientPerms = new ConcurrentHashMap<String, PermissionAttachment>();
	
	public VaultHook(Plugin plugin, ArimPerms core) {
		this.plugin = plugin;
		this.core = core;
	}
	
	private User getByUUID(UUID uuid) {
		return core.getUserByUUID(uuid);
	}
	
	private User getByPlayer(OfflinePlayer player) {
		return getByUUID(player.getUniqueId());
	}
	
	private UUID getPossibleId(String playerName) {
		for (Player player : plugin.getServer().getOnlinePlayers()) {
			if (player.getName().equalsIgnoreCase(playerName)) {
				return player.getUniqueId();
			}
		}
		return null;
	}
	
	private String getCallerClass() {
		return core.getRegistry().computeIfAbsent(CallerFinder.class, () -> new CallerFinderProvider()).getCallerClass(3).getName();
	}
	
	private boolean userHas(User user, String permission, String world) {
		return user.hasPermission(permission) || user.hasPermission(permission, world);
	}
	
	@Override
	public String getName() {
		return plugin.getDescription().getName();
	}
	
	@Override
	public boolean isEnabled() {
		return plugin.isEnabled();
	}
	
	@Override
	public boolean hasSuperPermsCompat() {
		return true;
	}
	
	@Override
	@Deprecated
	public boolean has(String world, String player, String permission) {
		return playerHas(world, player, permission);
	}
	
	@Override
	@Deprecated
	public boolean has(World world, String player, String permission) {
		return playerHas(world == null ? null : world.getName(), player, permission);
	}
	
	@Override
	public boolean has(CommandSender sender, String permission) {
		return !(sender instanceof Player) || has((Player) sender, permission);
	}
	
	@Override
	public boolean has(Player player, String permission) {
		return userHas(getByPlayer(player), permission, player.getWorld().getName());
	}
	
	@Override
	@Deprecated
	public boolean playerHas(String world, String player, String permission) {
		if (!core.config().getBoolean("bad-plugins.stringly-players.enable-compatibility")) {
			return false;
		} else if (core.config().getBoolean("bad-plugins.stringly-players.log-calls.enable")) {
			core.logs().verbose(core.config().getBoolean("bad-plugins.stringly-players.log-calls.log-caller-class") ? "An outdated plugin (" + getCallerClass() + ") called a deprecated method. Consider updating it." : "An outdated plugin called a deprecated method. Consider updating it.");
		}
		UUID uuid = getPossibleId(player);
		return uuid != null && userHas(getByUUID(uuid), permission, world);
	}
	
	@Override
	@Deprecated
	public boolean playerHas(World world, String player, String permission) {
		return playerHas(world == null ? null : world.getName(), player, permission);
	}
	
	@Override
	public boolean playerHas(String world, OfflinePlayer player, String permission) {
		return userHas(getByPlayer(player), permission, world);
	}
	
	@Override
	public boolean playerHas(Player player, String permission) {
		return has(player, permission);
	}
	
	@Override
	@Deprecated
	public boolean playerAdd(String world, String player, String permission) {
		return false;
	}
	
	@Override
	@Deprecated
	public boolean playerAdd(World world, String player, String permission) {
		return false;
	}
	
	@Override
	public boolean playerAdd(String world, OfflinePlayer player, String permission) {
		return false;
	}
	
	@Override
	public boolean playerAdd(Player player, String permission) {
		return false;
	}
	
	@Override
	public boolean playerAddTransient(OfflinePlayer player, String permission) {
		return player instanceof Player && playerAddTransient((Player) player, permission);
	}
	
	@Override
	public boolean playerAddTransient(Player player, String permission) {
		return transientPerms.computeIfAbsent(permission, (perm) -> player.addAttachment(plugin, perm, true)) != null;
	}
	
	@Override
	public boolean playerAddTransient(String worldName, OfflinePlayer player, String permission) {
		return player instanceof Player && playerAddTransient(worldName, (Player) player, permission);
	}
	
	@Override
	public boolean playerAddTransient(String worldName, Player player, String permission) {
		return playerAddTransient(player, permission);
	}
	
	@Override
	public boolean playerRemoveTransient(String worldName, OfflinePlayer player, String permission) {
		return player instanceof Player && playerRemoveTransient(worldName, (Player) player, permission);
	}
	
	@Override
	public boolean playerRemoveTransient(String worldName, Player player, String permission) {
		return playerRemoveTransient(player, permission);
	}
	
	@Override
	@Deprecated
	public boolean playerRemove(String world, String player, String permission) {
		return false;
	}
	
	@Override
	public boolean playerRemove(String world, OfflinePlayer player, String permission) {
		return false;
	}
	
	@Override
	@Deprecated
	public boolean playerRemove(World world, String player, String permission) {
		return false;
	}
	
	@Override
	public boolean playerRemove(Player player, String permission) {
		return false;
	}
	
	@Override
	public boolean playerRemoveTransient(OfflinePlayer player, String permission) {
		return player instanceof Player && playerRemoveTransient((Player) player, permission);
	}
	
	@Override
	public boolean playerRemoveTransient(Player player, String permission) {
		player.removeAttachment(transientPerms.get(permission));
		return true;
	}
	
	@Override
	public boolean groupHas(String world, String group, String permission) {
		Group groupId = core.groups().getGroup(group);
		return groupId.hasPermission(permission) || groupId.hasPermission(permission, world);
	}
	
	@Override
	public boolean groupHas(World world, String group, String permission) {
		return groupHas(world == null ? null : world.getName(), group, permission);
	}
	
	@Override
	public boolean groupAdd(String world, String group, String permission) {
		return core.groups().getGroup(group).addPermission(permission, world) || true;
	}
	
	@Override
	public boolean groupAdd(World world, String group, String permission) {
		return groupAdd(world == null ? null : world.getName(), group, permission);
	}
	
	@Override
	public boolean groupRemove(String world, String group, String permission) {
		return core.groups().getGroup(group).removePermission(permission, world) || true;
	}
	
	@Override
	public boolean groupRemove(World world, String group, String permission) {
		return groupRemove(world == null ? null : world.getName(), group, permission);
	}
	
	@Override
	@Deprecated
	public boolean playerInGroup(String world, String player, String group) {
		if (!core.config().getBoolean("bad-plugins.stringly-players.enable-compatibility")) {
			return false;
		} else if (core.config().getBoolean("bad-plugins.stringly-players.log-calls.enable")) {
			core.logs().verbose(core.config().getBoolean("bad-plugins.stringly-players.log-calls.log-caller-class") ? "An outdated plugin (" + getCallerClass() + ") called a deprecated method. Consider updating it." : "An outdated plugin called a deprecated method. Consider updating it.");
		}
		UUID uuid = getPossibleId(player);
		return uuid != null && CollectionsUtil.checkForAnyMatches(getByUUID(uuid).getGroups(), (groupId) -> groupId.getId().equalsIgnoreCase(group));
	}
	
	@Override
	@Deprecated
	public boolean playerInGroup(World world, String player, String group) {
		return playerInGroup(world == null ? null : world.getName(), player, group);
	}
	
	@Override
	public boolean playerInGroup(String world, OfflinePlayer player, String group) {
		return CollectionsUtil.checkForAnyMatches(getByPlayer(player).getGroups(), (groupId) -> groupId.getId().equalsIgnoreCase(group));
	}
	
	@Override
	public boolean playerInGroup(Player player, String group) {
		return playerInGroup(player.getWorld().getName(), player, group);
	}
	
	@Override
	@Deprecated
	public boolean playerAddGroup(String world, String player, String group) {
		if (!core.config().getBoolean("bad-plugins.stringly-players.enable-compatibility")) {
			return false;
		} else if (core.config().getBoolean("bad-plugins.stringly-players.log-calls.enable")) {
			core.logs().verbose(core.config().getBoolean("bad-plugins.stringly-players.log-calls.log-caller-class") ? "An outdated plugin (" + getCallerClass() + ") called a deprecated method. Consider updating it." : "An outdated plugin called a deprecated method. Consider updating it.");
		}
		UUID uuid = getPossibleId(player);
		return uuid != null && (getByUUID(uuid).addGroup(core.groups().getGroup(group)) || true);
	}
	
	@Override
	@Deprecated
	public boolean playerAddGroup(World world, String player, String group) {
		return playerAddGroup(world == null ? null : world.getName(), player, group);
	}
	
	@Override
	public boolean playerAddGroup(String world, OfflinePlayer player, String group) {
		return getByPlayer(player).addGroup(core.groups().getGroup(group)) || true;
	}
	
	@Override
	public boolean playerAddGroup(Player player, String group) {
		return playerAddGroup(player.getWorld().getName(), player, group);
	}
	
	@Override
	@Deprecated
	public boolean playerRemoveGroup(String world, String player, String group) {
		if (!core.config().getBoolean("bad-plugins.stringly-players.enable-compatibility")) {
			return false;
		} else if (core.config().getBoolean("bad-plugins.stringly-players.log-calls.enable")) {
			core.logs().verbose(core.config().getBoolean("bad-plugins.stringly-players.log-calls.log-caller-class") ? "An outdated plugin (" + getCallerClass() + ") called a deprecated method. Consider updating it." : "An outdated plugin called a deprecated method. Consider updating it.");
		}
		UUID uuid = getPossibleId(player);
		return uuid != null && (getByUUID(uuid).removeGroup(core.groups().getGroup(group)) || true);
	}
	
	@Override
	@Deprecated
	public boolean playerRemoveGroup(World world, String player, String group) {
		return playerRemoveGroup(world == null ? null : world.getName(), player, group);
	}
	
	@Override
	public boolean playerRemoveGroup(String world, OfflinePlayer player, String group) {
		return getByPlayer(player).removeGroup(core.groups().getGroup(group)) || true;
	}
	
	@Override
	public boolean playerRemoveGroup(Player player, String group) {
		return playerRemoveGroup(player.getWorld().getName(), player, group);
	}
	
	@Override
	@Deprecated
	public String[] getPlayerGroups(String world, String player) {
		if (!core.config().getBoolean("bad-plugins.stringly-players.enable-compatibility")) {
			return new String[] {};
		} else if (core.config().getBoolean("bad-plugins.stringly-players.log-calls.enable")) {
			core.logs().verbose(core.config().getBoolean("bad-plugins.stringly-players.log-calls.log-caller-class") ? "An outdated plugin (" + getCallerClass() + ") called a deprecated method. Consider updating it." : "An outdated plugin called a deprecated method. Consider updating it.");
		}
		UUID uuid = getPossibleId(player);
		return uuid != null ? getByUUID(uuid).getGroups().stream().map((group) -> group.getId()).toArray(String[]::new) : new String[] {};
	}
	
	@Override
	@Deprecated
	public String[] getPlayerGroups(World world, String player) {
		return getPlayerGroups(world == null ? null : world.getName(), player);
	}
	
	@Override
	public String[] getPlayerGroups(String world, OfflinePlayer player) {
		return getByPlayer(player).getGroups().stream().map((group) -> group.getId()).toArray(String[]::new);
	}
	
	@Override
	public String[] getPlayerGroups(Player player) {
		return getPlayerGroups(player.getWorld().getName(), player);
	}
	
	@Override
	@Deprecated
	public String getPrimaryGroup(String world, String player) {
		if (core.config().getBoolean("bad-plugins.stringly-players.log-calls.enable")) {
			core.logs().verbose(core.config().getBoolean("bad-plugins.stringly-players.log-calls.log-caller-class") ? "An outdated plugin (" + getCallerClass() + ") called a deprecated method. Consider updating it." : "An outdated plugin called a deprecated method. Consider updating it.");
		}
		// ArimPerms does not implement anything such as a "primary group"
		return "";
	}
	
	@Override
	@Deprecated
	public String getPrimaryGroup(World world, String player) {
		return getPrimaryGroup(world == null ? null : world.getName(), player);
	}
	
	@Override
	public String getPrimaryGroup(String world, OfflinePlayer player) {
		return "";
	}
	
	@Override
	public String getPrimaryGroup(Player player) {
		return "";
	}
	
	@Override
	public String[] getGroups() {
		return CollectionsUtil.convertAll(core.groups().getGroups().toArray(new Group[] {}), (group) -> group.getId());
	}
	
	@Override
	public boolean hasGroupSupport() {
		return true;
	}
	
}
