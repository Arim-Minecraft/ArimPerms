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

import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import space.arim.perms.api.ArimPerms;
import space.arim.perms.core.ArimPermsPlugin;
import space.arim.perms.core.PluginEnvOptions;

import net.milkbowl.vault.permission.Permission;

public class ArimPermsSpigot extends JavaPlugin {

	ArimPerms core;
	private Field playerField;
	
	@Override
	public void onEnable() {
		Class<?> serverClass = getServer().getClass();
		if (serverClass.getSimpleName().equals("CraftServer")) { // CraftBukkit
			String versionPrefix;
			if (serverClass.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
				versionPrefix = "org.bukkit.craftbukkit.";
			} else {
				versionPrefix = serverClass.getName().substring("org.bukkit.craftbukkit".length(), serverClass.getName().length() - "CraftServer".length());
				versionPrefix = serverClass.getName().substring("org.bukkit.craftbukkit".length());
				versionPrefix = versionPrefix.substring(0, versionPrefix.length() - "CraftServer".length());
			}
			try {
				playerField = Class.forName(versionPrefix + "entity.CraftHumanEntity").getDeclaredField("perm");
			} catch (NoSuchFieldException | SecurityException | ClassNotFoundException ex) {
				throw new IllegalStateException("Could not initialise permissible injector!", ex);
			}
		//} else if (serverClass.getSimpleName().equals("GlowServer")) { // Glowstone
			
		} else {
			throw new IllegalStateException("Your server is not running CraftBukkit or Glowstone. Please contact A248 (a248@arim.space) to update ArimPerms for your server version.");
		}
		core = new ArimPermsPlugin(new PluginEnvOptions(getDataFolder(), getLogger(), getServer().getOnlineMode()));
		core.reload(true);
		getServer().getServicesManager().register(Permission.class, new VaultHook(this, core), this, ServicePriority.High);
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
	}
	
	@Override
	public void onDisable() {
		core.close();
	}
	
	PlayerInjector getInjector(Player player) {
		return new PlayerInjector(player, Objects.requireNonNull(playerField, "Plugin not initialised!"));
	}
	
	PermissibleReplacement getInjection(Player player) {
		return new PermissibleReplacement(this, core.users().getUser(player.getUniqueId().toString().replace("-", "")), player);
	}
	
}
