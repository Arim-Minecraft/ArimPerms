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

import net.md_5.bungee.api.plugin.Plugin;

import space.arim.universal.registry.UniversalRegistry;

import space.arim.api.concurrent.AsyncExecution;
import space.arim.api.concurrent.SyncExecution;
import space.arim.api.server.bungee.DefaultAsyncExecution;
import space.arim.api.server.bungee.DefaultSyncExecution;
import space.arim.api.server.bungee.DefaultUUIDResolver;
import space.arim.api.uuid.UUIDResolver;

import space.arim.perms.api.ArimPerms;
import space.arim.perms.core.ArimPermsPlugin;
import space.arim.perms.core.PluginEnvOptions;

public class ArimPermsBungee extends Plugin {

	ArimPerms core;
	
	@Override
	public void onLoad() {
		UniversalRegistry.get().computeIfAbsent(AsyncExecution.class, () -> new DefaultAsyncExecution(this));
		UniversalRegistry.get().computeIfAbsent(SyncExecution.class, () -> new DefaultSyncExecution(this));
		UniversalRegistry.get().computeIfAbsent(UUIDResolver.class, () -> new DefaultUUIDResolver(this));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		core = new ArimPermsPlugin(new PluginEnvOptions(UniversalRegistry.get(), getDataFolder(), getLogger(), getProxy().getConfig().isOnlineMode()));
		core.reload(true);
		getProxy().getPluginManager().registerListener(this, new PlayerListener(this));
	}
	
}
