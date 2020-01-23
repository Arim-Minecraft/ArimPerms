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

import space.arim.api.sql.ExecutableQuery;

class Query {
	
	private final PreQuery statement;
	private final Object[] parameters;
	
	Query(PreQuery statement, Object...parameters) {
		this.statement = statement;
		this.parameters = parameters;
	}
	
	ExecutableQuery convertToExecutable(String prefix) {
		return new ExecutableQuery(statement.eval(prefix), parameters);
	}
	
	enum PreQuery {
		CREATE_TABLE_GROUPS(
				"CREATE TABLE IF NOT EXISTS `%PREFIX%groups` ("
						+ "`id` VARCHAR(31) NOT NULL,"
						+ "`parents` TINYTEXT NOT NULL,"
						+ "`permissions` MEDIUMTEXT NOT NULL)"),

		CREATE_TABLE_USERS(
				"CREATE TABLE IF NOT EXISTS `%PREFIX%users` ("
						+ "`id` VARCHAR(31) NOT NULL,"
						+ "`groups` TINYTEXT NOT NULL)"),
		
		REPLACE_GROUP(
				"REPLACE INTO `%PREFIX%groups` "
						+ "(`id`, `parents`, `permissions`) "
						+ "VALUES (?, ?, ?)"),

		REPLACE_USER(
				"REPLACE INTO `%PREFIX%users` "
						+ "(`id`, `groups`) "
						+ "VALUES (?, ?)"),
		
		SELECT_GROUPS("SELECT * FROM `%PREFIX%groups`"),
		
		SELECT_USERS("SELECT * FROM `%PREFIX%users`");

		private String mysql;

		private PreQuery(String mysql) {
			this.mysql = mysql;
		}
		
		String eval(String prefix) {
			return mysql.replace("%PREFIX%", prefix);
		}
		
		@Override
		public String toString() {
			return mysql;
		}
	}
}
