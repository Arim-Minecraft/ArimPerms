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
package space.arim.perms.api;

import java.io.File;

import space.arim.api.config.SimpleConfigFramework;
import space.arim.api.uuid.UUIDResolver;

/**
 * A more specific subclass of {@link ArimPermsApi} intended to use all or much of the ArimPerms default implementation. <br>
 * <br>
 * Methods in this class are used for specific implementations and may be subject to change as they are not considered part of the official API.
 * 
 * @author A248
 *
 */
public interface ArimPerms extends ArimPermsApi {
	
	/**
	 * The plugin's configuration folder
	 * 
	 * @return the folder file
	 */
	File getFolder();
	
	/**
	 * Used for internal plugin logging
	 * 
	 * @return a logs manager
	 */
	LogManager logs();
	
	/**
	 * Used for internal command execution
	 * 
	 * @return a commands manager
	 */
	CommandManager commands();
	
	/**
	 * Handles the saving and loading of users and groups to and from
	 * the local file system or the MySQL database, depending on configuration.
	 * 
	 * @return the data manager
	 */
	DataManager data();
	
	/**
	 * A {@link UUIDResolver} used for commands.
	 * 
	 * @return the uuid resolver
	 */
	UUIDResolver resolver();
	
	/**
	 * The configuration corresponding to the config.yml
	 * 
	 * @return a {@link SimpleConfigFramework}
	 */
	SimpleConfigFramework config();
	
	/**
	 * The configuration correspondign to the messages.yml
	 * 
	 * @return a {@link SimpleConfigFramework}
	 */
	SimpleConfigFramework messages();
	
	@Override
	default void reload(boolean first) {
		config().reload();
		messages().reload();
		logs().reload(first);
		data().reload(first);
		groups().reload(first);
		users().reload(first);
		data().closeDb();
	}
	
	@Override
	default void close() {
		users().close();
		groups().close();
		data().closeDb();
		data().close();
		logs().close();
	}
	
}
