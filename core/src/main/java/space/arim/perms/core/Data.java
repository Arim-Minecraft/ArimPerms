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
import java.util.Collection;
import java.util.HashSet;

import space.arim.api.sql.SimpleLoggingSql;

import space.arim.perms.api.ArimPerms;
import space.arim.perms.api.DataManager;
import space.arim.perms.api.Group;
import space.arim.perms.api.User;

public class Data extends SimpleLoggingSql implements DataManager {

	private final ArimPerms core;
	
	private static final String DEFAULTING_TO_STORAGE_MODE = "Invalid storage mode specified! Defaulting to FILE...";
	
	private Connection connection;
	private boolean mysql = false;
	
	public Data(ArimPerms core) {
		this.core = core;
	}
	
	private void connect() throws SQLException {
		if (mysql) {
			connection = DriverManager.getConnection(core.config().getString("storage.mysql.url").replace("%HOST%", core.config().getString("storage.mysql.host")).replace("%PORT%", Integer.toString(core.config().getInt("storage.mysql.port"))).replace("%DATABASE%", core.config().getString("storage.mysql.database")), core.config().getString("storage.mysql.username"), core.config().getString("storage.mysql.password"));
		} else {
			connection = DriverManager.getConnection(core.config().getString("storage.file.url"), "SA", "");
		}
	}

	@Override
	protected Connection getUnderlyingConnection() {
		return connection;
	}
	
	@Override
	protected void log(String message) {
		core.logs().verbose(message);
	}
	
	private String getStorageModeName() {
		return mysql ? "mysql" : "file";
	}
	
	private boolean parseMySql(String input) {
		switch (input.toLowerCase()) {
		case "hsqldb":
		case "local":
		case "file":
		case "sqlite":
			return false;
		case "mysql":
		case "sql":
			return true;
		default:
			core.logs().warning(DEFAULTING_TO_STORAGE_MODE);
			return false;
		}
	}
	
	@Override
	public void reload(boolean first) {
		mysql = parseMySql(core.config().getString("storage.mode"));
		
		if (first || core.config().getBoolean("storage.restart-on-reload")) {
			core.logs().verbose("Loading data backend " + getStorageModeName());
			try {
				connect();
			} catch (SQLException ex) {
				core.logs().exception("Failed to connect to storage mode " + getStorageModeName(), ex);
			}
		}
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

	@Override
	public Collection<Group> loadGroups() {
		return new HashSet<Group>();
	}

	@Override
	public Collection<User> loadUsers() {
		return new HashSet<User>();
	}

	@Override
	public void saveGroups(Collection<Group> groups) {
		
	}

	@Override
	public void saveUsers(Collection<User> users) {
		
	}
	
}
