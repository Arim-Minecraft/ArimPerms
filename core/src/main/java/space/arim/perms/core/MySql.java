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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import space.arim.universal.util.lang.AutoClosable;

import space.arim.api.sql.SimpleLoggingSql;

import space.arim.perms.api.ArimPerms;

class MySql extends SimpleLoggingSql implements AutoClosable {

	private final ArimPerms core;
	
	private Connection connection;
	
	MySql(ArimPerms core) {
		this.core = core;
	}
	
	@Override
	protected Connection getUnderlyingConnection() {
		return connection;
	}
	
	@Override
	protected void log(String message) {
		core.logs().verbose(message);
	}
	
	boolean start() {
		try {
			connection = DriverManager.getConnection(core.config().getString("storage.mysql.url").replace("%HOST%", core.config().getString("storage.mysql.host")).replace("%PORT%", Integer.toString(core.config().getInt("storage.mysql.port"))).replace("%DATABASE%", core.config().getString("storage.mysql.database")), core.config().getString("storage.mysql.username"), core.config().getString("storage.mysql.password"));
			return true;
		} catch (SQLException ex) {
			core.logs().exception("Failed to connect to MySQL database!", ex);
		}
		return false;
	}
	
	@Override
	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException ex) {
				core.logs().exception("Failed to close database connection", ex);
			}
		}
	}
	
}
