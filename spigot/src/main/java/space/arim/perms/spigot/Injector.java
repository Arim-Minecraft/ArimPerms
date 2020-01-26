/* 
 * ArimPerms-spigot
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * ArimPerms-spigot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ArimPerms-spigot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ArimPerms-spigot. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.perms.spigot;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.PermissionAttachment;

abstract class Injector<T> {

	final T target;
	final Field field;
	
	Injector(T target, Field field) {
		this.target = target;
		this.field = field;
		field.setAccessible(true);
	}
	
	/**
	 * Copies attachments from a removed Permissible to the updated one
	 * 
	 * @param updated the new PermissibleReplacement to use
	 * @param attachments the attachment list
	 */
	void copyAttachments(PermissibleReplacement updated, List<PermissionAttachment> attachments) {
		attachments.forEach(updated::addForeign);
		updated.recalculatePermissions();
	}
	
	@SuppressWarnings("unchecked")
	boolean inject(PermissibleReplacement updated) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Permissible previous = (Permissible) field.get(target);
		updated.setPrevious(previous);
		if (previous instanceof PermissibleBase && !(previous instanceof PermissibleReplacement)) {
			Field attachments = PermissibleBase.class.getDeclaredField("attachments");
			attachments.setAccessible(true);
			copyAttachments(updated, (List<PermissionAttachment>) attachments.get(previous));
			field.set(target, updated);
			return true;
		}
		field.set(target, updated);
		return false;
	}
	
	boolean uninject() throws IllegalArgumentException, IllegalAccessException {
		Object permissible = field.get(target);
		if (permissible instanceof PermissibleReplacement) {
			field.set(target, ((PermissibleReplacement) permissible).getPrevious());
			return true;
		}
		return false;
	}
	
}
