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

import java.util.Collection;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Maintains the central collection of users. <br>
 * <br>
 * <b>Specifications</b>: <br>
 * Method implementations must be thread safe. <br>
 * {@link #getUsers()} should return a collection in which changes are automatically reflected.
 * 
 * @author A248
 *
 */
public interface UserManager extends Configurable {

	/**
	 * Gets a User by id, creating a new user if the specified user is not tracked.
	 * 
	 * @param id the user's ID
	 * @return a user, never <code>null</code>
	 */
	User getUser(String id);
	
	/**
	 * Gets a possible user by id
	 * 
	 * @param id the user id
	 * @return the user if the user is tracked, <code>null</code> otherwise
	 */
	@Nullable
	User getPossibleUser(String id);
	
	/**
	 * Adds a User, overriding potential existing entires.
	 * 
	 * @param user the user to add
	 * @return true if no user existed with {@link User#getId()}, false otherwise
	 */
	boolean addUser(User user);
	
	/**
	 * Gets all users tracked. The returned Collection automatically reflects changes such as user additions.
	 * 
	 * @return an immutable collection view of all users
	 */
	Collection<User> getUsers();
	
}
