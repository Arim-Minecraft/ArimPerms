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

import java.sql.ResultSet;
import java.sql.SQLException;

class RawUser {
	
	private final String id;
	private final String groups;
	
	private RawUser(String id, String groups) {
		this.id = id;
		this.groups = groups;
	}
	
	RawUser(ResultSet data) throws SQLException {
		this(data.getString("id"), data.getString("groups"));
	}
	
	static RawUser from(String id, String groups) {
		return groups.isEmpty() ? null : new RawUser(id, groups);
	}
	
	String getId() {
		return id;
	}
	
	String getGroups() {
		return groups;
	}
	
	Query query() {
		return new Query(Query.PreQuery.REPLACE_USER, id, groups);
	}
	
	@Override
	public String toString() {
		return id + "|" + groups;
	}
	
}
