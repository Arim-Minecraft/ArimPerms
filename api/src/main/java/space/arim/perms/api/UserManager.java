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
 * Maintains the central collection of users
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
	 * @return false if a user already existed with {@link User#getId()}, true otherwise
	 */
	boolean addUser(User user);
	
	/**
	 * Gets all users tracked.
	 * 
	 * @return a collection of all users
	 */
	Collection<User> getUsers();
	
}
