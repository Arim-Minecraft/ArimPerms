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

import java.lang.reflect.Field;
import java.util.Objects;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import space.arim.universal.registry.UniversalRegistry;

import space.arim.api.concurrent.AsyncExecution;
import space.arim.api.concurrent.SyncExecution;
import space.arim.api.server.PluginInformation;
import space.arim.api.server.bukkit.DefaultAsyncExecution;
import space.arim.api.server.bukkit.DefaultSyncExecution;
import space.arim.api.server.bukkit.DefaultUUIDResolver;
import space.arim.api.uuid.UUIDResolver;

import space.arim.perms.api.ArimPerms;
import space.arim.perms.core.ArimPermsPlugin;
import space.arim.perms.core.PluginEnvOptions;

import net.milkbowl.vault.permission.Permission;

public class ArimPermsSpigot extends JavaPlugin {

	ArimPerms core;
	private Field playerField;
	
	@Override
	public void onLoad() {
		UniversalRegistry.get().computeIfAbsent(AsyncExecution.class, () -> new DefaultAsyncExecution(this));
		UniversalRegistry.get().computeIfAbsent(SyncExecution.class, () -> new DefaultSyncExecution(this));
		UniversalRegistry.get().computeIfAbsent(UUIDResolver.class, () -> new DefaultUUIDResolver(this));
	}
	
	@Override
	public void onEnable() {
		Class<?> serverClass = getServer().getClass();
		if (serverClass.getSimpleName().equals("CraftServer")) { // CraftBukkit
			try {
				playerField = Class.forName(serverClass.getName().substring(0, serverClass.getName().length() - "CraftServer".length()) + "entity.CraftHumanEntity").getDeclaredField("perm");
			} catch (NoSuchFieldException | SecurityException | ClassNotFoundException ex) {
				throw new IllegalStateException("Could not determine injection target field!", ex);
			}
		} else if (serverClass.getSimpleName().equals("GlowServer")) { // Glowstone
			try {
				playerField = Class.forName("net.glowstone.entity.GlowHumanEntity").getDeclaredField("permissions");
			} catch (NoSuchFieldException | SecurityException | ClassNotFoundException ex) {
				throw new IllegalStateException("Could not determine injection target field!", ex);
			}
		} else {
			throw new IllegalStateException("Your server is not running CraftBukkit or Glowstone. Please contact A248 (a248@arim.space) to update ArimPerms for your server version.");
		}
		playerField.setAccessible(true);
		core = new ArimPermsPlugin(UniversalRegistry.get(), PluginInformation.getForSpigot(getDescription()), new PluginEnvOptions(getDataFolder(), getLogger(), getServer().getOnlineMode()));
		core.reload(true);
		getServer().getServicesManager().register(Permission.class, new VaultHook(this, core), this, ServicePriority.High);
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
	}
	
	@Override
	public void onDisable() {
		core.close();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return core.commands().execute(new WrappedSender(sender), args);
	}
	
	PlayerInjector getInjector(Player player) {
		return new PlayerInjector(player, Objects.requireNonNull(playerField, "Plugin not initialised!"));
	}
	
	PermissibleReplacement getInjection(Player player) {
		return new PermissibleReplacement(this, core.getUserByUUID(player.getUniqueId()), player);
	}
	
}
