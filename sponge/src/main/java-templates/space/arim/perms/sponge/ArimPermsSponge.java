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

import java.io.File;
import java.util.logging.Logger;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.AsynchronousExecutor;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.scheduler.SynchronousExecutor;

import com.google.inject.Inject;

import space.arim.universal.registry.UniversalRegistry;

import space.arim.api.concurrent.AsyncExecution;
import space.arim.api.concurrent.SyncExecution;
import space.arim.api.platform.sponge.DecoupledCommand;
import space.arim.api.platform.sponge.DefaultAsyncExecution;
import space.arim.api.platform.sponge.DefaultSyncExecution;
import space.arim.api.platform.sponge.DefaultUUIDResolver;
import space.arim.api.platform.sponge.SpongePlatform;
import space.arim.api.uuid.UUIDResolver;

import space.arim.perms.api.ArimPerms;
import space.arim.perms.core.ArimPermsPlugin;
import space.arim.perms.core.PluginEnvOptions;

@Plugin(id = "${plugin.spongeid}", name = "${plugin.name}", version = "${plugin.version}", authors = {"${plugin.author}"}, description = "${plugin.description}", url = "${plugin.url}", dependencies = {@Dependency(id = "arimapiplugin")})
public class ArimPermsSponge extends DecoupledCommand {
	
	@Inject
	@ConfigDir(sharedRoot=false)
	private File folder;
	
	ArimPerms core;
	
	private PluginContainer getPlugin() {
		return Sponge.getPluginManager().fromInstance(this).get();
	}
	
	@Inject
	public ArimPermsSponge(@AsynchronousExecutor SpongeExecutorService async, @SynchronousExecutor SpongeExecutorService sync) {
		sync.execute(() -> {
			PluginContainer plugin = getPlugin();
			UniversalRegistry.get().computeIfAbsent(AsyncExecution.class, () -> new DefaultAsyncExecution(plugin, async));
			UniversalRegistry.get().computeIfAbsent(SyncExecution.class, () -> new DefaultSyncExecution(plugin, sync));
			UniversalRegistry.get().computeIfAbsent(UUIDResolver.class, () -> new DefaultUUIDResolver(plugin));
		});
	}
	
	@Listener
	public void onEnable(@SuppressWarnings("unused") GamePreInitializationEvent evt) {		
		Logger logger = Logger.getLogger("${plugin.spongeid}");
		logger.setParent(Logger.getLogger(""));
		core = new ArimPermsPlugin(UniversalRegistry.get(), SpongePlatform.get().convertPluginInfo(getPlugin()), new PluginEnvOptions(folder, logger, Sponge.getServer().getOnlineMode()));
		core.reload(true);
		Sponge.getEventManager().registerListeners(this, new PlayerListener(this));
		Sponge.getCommandManager().register(this, this, "arimperms", "ap");
		// TODO Fully implement PermissionsService
		// Sponge.getServiceManager().setProvider(this, PermissionService.class, new SpongeHook(core));
	}
	
	@Listener
	public void onDisable(@SuppressWarnings("unused") GameStoppingServerEvent evt) {
		core.close();
		Sponge.getEventManager().unregisterPluginListeners(this);
	}
	
	@Override
	protected boolean execute(CommandSource sender, String[] args) {
		return core.commands().execute(new WrappedSender(sender), args);
	}
	
}
