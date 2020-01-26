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

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import space.arim.api.concurrent.AsyncExecution;

import space.arim.perms.api.User;

class PermissibleReplacement extends PermissibleBase {
	
	private final ArimPermsSpigot plugin;
	private final User user;
	private final Player player;
	
	private final PermissionAttachmentReplacement self;
	
	private Permissible previous;
	
	private final Set<PermissionAttachment> attachments = ConcurrentHashMap.newKeySet();
	private Set<String> attached = Collections.emptySet();
	
	PermissibleReplacement(ArimPermsSpigot plugin, User user, Player player) {
		super(player);
		this.plugin = plugin;
		this.user = user;
		this.player = player;
		self = new PermissionAttachmentReplacement(plugin, player, user);
	}
	
	void setPrevious(Permissible previous) {
		this.previous = previous;
	}
	
	Permissible getPrevious() {
		return previous;
	}
	
	@Override
	public boolean isOp() {
		// disabled OP
		return false;
	}
	
	@Override
	public void setOp(boolean op) {
		// disabled OP
	}
	
	@Override
	public boolean isPermissionSet(String permission) {
		return hasPermission(permission);
	}
	
	@Override
	public boolean isPermissionSet(Permission permission) {
		return hasPermission(permission.getName());
	}
	
	@Override
	public boolean hasPermission(String permission) {
		return user.hasPermission(permission) || user.hasPermission(permission, player.getWorld().getName()) || attached.contains(permission);
	}
	
	@Override
	public boolean hasPermission(Permission permission) {
		return hasPermission(permission.getName());
	}
	
	void addForeign(PermissionAttachment attachment) {
		PermissionAttachment converted = new PermissionAttachment(attachment.getPlugin(), this);
		attachment.getPermissions().forEach(converted::setPermission);
		converted.setRemovalCallback(attachment.getRemovalCallback());
		attachments.add(converted);
	}
	
	private void addAttachment(PermissionAttachment attachment) {
		attachments.add(attachment);
		recalculatePermissions();
	}
	
	private PermissionAttachment getAttachment(Plugin plugin) {
		return new PermissionAttachment(Objects.requireNonNull(plugin, "Plugin must not be null"), this);
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
		PermissionAttachment attachment = getAttachment(plugin);
		attachment.setPermission(name, value);
		addAttachment(attachment);
		return attachment;
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		PermissionAttachment attachment = getAttachment(plugin);
		addAttachment(attachment);
		return attachment;
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
		PermissionAttachment attachment = getAttachment(plugin);
		attachment.setPermission(name, value);
		addAttachment(attachment);
		this.plugin.core.getRegistry().getRegistration(AsyncExecution.class).runTaskLater(() -> removeAttachment(attachment), ticks*500);
		return attachment;
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		PermissionAttachment attachment = getAttachment(plugin);
		addAttachment(attachment);
		this.plugin.core.getRegistry().getRegistration(AsyncExecution.class).runTaskLater(() -> removeAttachment(attachment), ticks*500);
		return attachment;
	}
	
	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		attachments.remove(attachment);
	}
	
	@Override
	public void recalculatePermissions() {
		Set<String> attached = new HashSet<String>();
		attachments.forEach((attachment) -> {
			attachment.getPermissions().forEach((perm, value) -> {
				if (value) {
					attached.add(perm);
				}
			});
		});
		this.attached = Collections.unmodifiableSet(attached);
	}
	
	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		Set<PermissionAttachmentInfo> results = new HashSet<PermissionAttachmentInfo>();
		attachments.forEach((attachment) -> {
			attachment.getPermissions().forEach((perm, value) -> {
				results.add(new PermissionAttachmentInfo(this, perm, attachment, value));
			});
		});
		user.getEffectivePermissions().forEach((perm) -> {
			results.add(new PermissionAttachmentInfo(player, perm, self, true));
		});
		user.getEffectivePermissions(player.getWorld().getName()).forEach((perm) -> {
			results.add(new PermissionAttachmentInfo(player, perm, self, true));
		});
		return results;
	}

}
