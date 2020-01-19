/* 
 * ArimPerms-api
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * ArimPerms-api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ArimPerms-api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ArimPerms-api. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.perms.api;

import space.arim.universal.util.lang.AutoClosable;

/**
 * Represents some component of ArimPerms which may use configuration values
 * 
 * @author A248
 *
 */
public interface Configurable extends AutoClosable {

	/**
	 * Instructs the object to reread its configuration if it has cached any values.
	 * 
	 * @param first whether this is the first time the plugin is loading. <code>true</code> should only be used once, on program startup.
	 */
	default void reload(boolean first) {
		
	}
	
}
