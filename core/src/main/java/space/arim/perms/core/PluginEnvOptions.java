package space.arim.perms.core;

import java.io.File;
import java.util.logging.Logger;

import space.arim.universal.registry.Registry;

/**
 * Wrapper for environment specifications.
 * 
 * @author A248
 *
 */
public final class PluginEnvOptions {

	public final Registry registry;
	public final File folder;
	public final Logger logger;
	public final boolean onlineMode;
	
	public PluginEnvOptions(Registry registry, File folder, Logger logger, boolean onlineMode) {
		this.registry = registry;
		this.folder = folder;
		this.logger = logger;
		this.onlineMode = onlineMode;
	}
	
}