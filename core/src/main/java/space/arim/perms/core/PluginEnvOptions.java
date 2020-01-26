package space.arim.perms.core;

import java.io.File;
import java.util.logging.Logger;

/**
 * Wrapper for environment specifications.
 * 
 * @author A248
 *
 */
public final class PluginEnvOptions {

	public final File folder;
	public final Logger logger;
	public final boolean onlineMode;
	
	public PluginEnvOptions(File folder, Logger logger, boolean onlineMode) {
		this.folder = folder;
		this.logger = logger;
		this.onlineMode = onlineMode;
	}
	
}