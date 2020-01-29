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

class RawGroup {
	
	private final String id;
	private final String parents;
	private final String permissions;
	
	RawGroup(String id, String parents, String permissions) {
		this.id = id;
		this.parents = parents;
		this.permissions = permissions;
	}
	
	RawGroup(ResultSet data) throws SQLException {
		this(data.getString("id"), data.getString("parents"), data.getString("permissions"));
	}
	
	String getId() {
		return id;
	}
	
	String getParents() {
		return parents;
	}
	
	String getPermissions() {
		return permissions;
	}
	
	Query query() {
		return new Query(Query.PreQuery.REPLACE_GROUP, id, parents, permissions);
	}
	
	@Override
	public String toString() {
		return id + "|" + toStringWithoutId();
	}
	
	String toStringWithoutId() {
		return parents + "|" + permissions;
	}
	
}
