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
 * Maintains the central collection of groups. <br>
 * <br>
 * <b>Specifications</b>: <br>
 * Method implementations must be thread safe. <br>
 * {@link #getGroups()} should return a collection in which changes are automatically reflected.
 * 
 * @author A248
 *
 */
public interface GroupManager extends Configurable {
	
	/**
	 * Gets a Group by id, creating a new group if the specified group is not tracked.
	 * 
	 * @param id the group's ID
	 * @return a group, never <code>null</code>
	 */
	Group getGroup(String id);
	
	/**
	 * Gets a possible group by id
	 * 
	 * @param id the group id
	 * @return the group if the group is tracked, <code>null</code> otherwise
	 */
	@Nullable
	Group getPossibleGroup(String id);
	
	/**
	 * Adds a Group, overriding potential existing entires.
	 * 
	 * @param group the group to add
	 * @return true if no group existed with {@link Group#getId()}, false otherwise
	 */
	boolean addGroup(Group group);
	
	/**
	 * Gets all groups tracked. The returned Collection automatically reflects changes such as group additions.
	 * 
	 * @return an immutable collection view of all groups
	 */
	Collection<Group> getGroups();
	
}
