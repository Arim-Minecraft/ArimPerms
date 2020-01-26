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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

import space.arim.perms.api.User;

public class PermissionAttachmentReplacement extends PermissionAttachment {

	private final User user;
	
	public PermissionAttachmentReplacement(Plugin plugin, Player player, User user) {
		super(plugin, player);
		this.user = user;
	}
	
	@Override
	public Map<String, Boolean> getPermissions() {
		Map<String, Boolean> perms = new HashMap<String, Boolean>();
		user.getEffectivePermissions().forEach((perm) -> perms.put(perm, true));
		user.getEffectivePermissions(((Player) getPermissible()).getWorld().getName()).forEach((perm) -> perms.put(perm, true));
		return perms;
	}
	
	// ArimPerms is designed to use group permissions, not user permissions
	// Accordingly, these methods do nothing
	
	@Override
	public void setPermission(String name, boolean value) {
		
	}

	@Override
	public void setPermission(Permission perm, boolean value) {
		
	}

	@Override
	public void unsetPermission(String name) {
		
	}

	@Override
	public void unsetPermission(Permission perm) {
		
	}
	
	@Override
	public boolean remove() {
		return true;
	}
	
}
