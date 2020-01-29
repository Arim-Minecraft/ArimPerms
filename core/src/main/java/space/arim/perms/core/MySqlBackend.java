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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import space.arim.api.sql.ExecutableQuery;
import space.arim.api.sql.SimpleLoggingSql;

import space.arim.perms.api.ArimPerms;

class MySqlBackend extends SimpleLoggingSql implements Backend {

	private final ArimPerms core;
	private final String table_prefix;
	
	private Connection connection;
	
	MySqlBackend(ArimPerms core, String table_prefix) {
		this.core = core;
		this.table_prefix = table_prefix;
	}
	
	@Override
	protected Connection getUnderlyingConnection() {
		return connection;
	}
	
	@Override
	protected void log(String message) {
		core.logs().verbose(message);
	}
	
	@Override
	public boolean start() {
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

	@Override
	public Set<RawGroup> loadRawGroups() {
		HashSet<RawGroup> rawGroups = new HashSet<RawGroup>();
		try {
			ResultSet data = selectionQuery(Query.PreQuery.SELECT_GROUPS.eval(table_prefix));
			while (data.next()) {
				rawGroups.add(new RawGroup(data));
			}
			return rawGroups;
		} catch (SQLException ex) {
			core.logs().exception("Could not load groups from remote database! Using FILE storage...", ex);
			return null;
		}
	}
	
	@Override
	public Set<RawUser> loadRawUsers() {
		HashSet<RawUser> rawUsers = new HashSet<RawUser>();
		try {
			ResultSet data = selectionQuery(Query.PreQuery.SELECT_USERS.eval(table_prefix));
			while (data.next()) {
				rawUsers.add(new RawUser(data));
			}
			return rawUsers;
		} catch (SQLException ex) {
			core.logs().exception("Could not load users from remote database! Using FILE storage...", ex);
			return null;
		}
	}
	
	@Override
	public boolean saveRawGroups(Stream<RawGroup> groups) {
		try {
			executionQueries(groups.map(this::executeRawGroup).collect(Collectors.toSet()));
			return true;
		} catch (SQLException ex) {
			core.logs().exception("Could not save groups to remote database! Using FILE storage...", ex);
			return false;
		}
	}
	
	@Override
	public boolean saveRawUsers(Stream<RawUser> users) {
		try {
			executionQueries(users.map(this::executeRawUser).collect(Collectors.toSet()));
			return true;
		} catch (SQLException ex) {
			core.logs().exception("Could not save users to remote database! Using local storage...", ex);
			return false;
		}
	}
	
	private ExecutableQuery executeRawGroup(RawGroup group) {
		return group.query().convertToExecutable(table_prefix);
	}
	
	private ExecutableQuery executeRawUser(RawUser user) {
		return user == null ? null : user.query().convertToExecutable(table_prefix);
	}
	
}
