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

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import space.arim.api.server.LogFormatter;
import space.arim.api.util.FilesUtil;

import space.arim.perms.api.ArimPerms;
import space.arim.perms.api.LogManager;

public class Logs implements LogManager {

	private final ArimPerms core;
	private final Logger environmentLogger;
	
	private Logger logger;
	private FileHandler verboseLog;
	private FileHandler infoLog;
	private FileHandler errorLog;
	
	private int log_directory_keep_alive = 20;
	
	public Logs(ArimPerms core, Logger environmentLogger) {
		this.core = core;
		this.environmentLogger = environmentLogger;
	}
	
	private boolean enabled() {
		return logger != null;
	}
	
	private Logger getLogger() {
		return enabled() ? logger : environmentLogger;
	}
	
	@Override
	public void verbose(String message) {
		if (enabled()) {
			logger.log(Level.FINE, message);
		}
	}
	
	@Override
	public void info(String message) {
		getLogger().log(Level.INFO, message);
	}
	
	@Override
	public void warning(String message) {
		if (enabled()) {
			logger.log(Level.WARNING, message);
		}
		environmentLogger.log(Level.WARNING, message);
	}

	@Override
	public void exception(String message, Exception ex) {
		environmentLogger.log(Level.WARNING, "Encountered an error! Please check the plugin log for details (if the plugin log is disabled, the error will be in console).", ex);
		getLogger().log(Level.WARNING, message, ex);
	}
	
	private void checkDeleteLogs() {
		long keepAlive = 86_400_000L * log_directory_keep_alive;
		long current = System.currentTimeMillis();
		File[] logDirs = (new File(core.getFolder(), "logs")).listFiles();
		if (logDirs == null) {
			warning("Could not clean and delete old log folders");
			return;
		}
		for (File dir : logDirs) {
			if (dir.isDirectory() && current - dir.lastModified() > keepAlive) {
				if (dir.delete()) {
					verbose("Successfully cleaned & deleted old log folder " + dir.getPath());
				} else {
					verbose("Could not clean & delete old log folder " + dir.getPath());
				}
			}
		}
	}
	
	@Override
	public void reload(boolean first) {
		log_directory_keep_alive = core.config().getInt("logs.log-directory-keep-alive");
		if (first) {
			File path = FilesUtil.dateSuffixedFile(core.getFolder(), "", "logs");
			try {
				if (!path.exists() && !path.mkdirs()) {
					environmentLogger.warning("Failed to create logs directory!");
					return;
				}
				verboseLog = new FileHandler(path + File.separator + "verbose.log");
				infoLog = new FileHandler(path + File.separator + "info.log");
				errorLog = new FileHandler(path + File.separator + "error.log");
				Formatter universalFormatter = new LogFormatter();
				verboseLog.setFormatter(universalFormatter);
				infoLog.setFormatter(universalFormatter);
				errorLog.setFormatter(universalFormatter);
				verboseLog.setLevel(Level.ALL);
				infoLog.setLevel(Level.CONFIG);
				errorLog.setLevel(Level.WARNING);
				logger = Logger.getLogger("ArimPerms-Core");
				logger.setParent(environmentLogger);
				logger.setUseParentHandlers(false);
				logger.addHandler(verboseLog);
				logger.addHandler(infoLog);
				logger.addHandler(errorLog);
				info("Plugin logging initialised in " + path);
			} catch (IOException ex) {
				warning("Plugin log initialisation failed!");
			}
			checkDeleteLogs();
		}
	}
	
}
