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

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import space.arim.perms.api.ArimPerms;
import space.arim.perms.api.User;
import space.arim.perms.api.UserManager;

public class Users implements UserManager {

	private final ArimPerms core;
	
	private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<String, User>();
	private Collection<User> usersView;
	
	public Users(ArimPerms core) {
		this.core = core;
	}
	
	@Override
	public User getUser(String id) {
		return users.computeIfAbsent(id, (userId) -> new UserInfo(userId));
	}
	
	@Override
	public User getPossibleUser(String id) {
		return users.get(id);
	}
	
	@Override
	public boolean addUser(User user) {
		return users.put(user.getId(), user) != null;
	}
	
	@Override
	public Collection<User> getUsers() {
		return usersView != null ? usersView : (usersView = Collections.unmodifiableCollection(users.values()));
	}
	
	@Override
	public void reload(boolean first) {
		if (first) {
			core.data().loadUsers().forEach(this::addUser);
		}
	}
	
	@Override
	public void close() {
		core.data().saveUsers(users.values());
	}
	
}
