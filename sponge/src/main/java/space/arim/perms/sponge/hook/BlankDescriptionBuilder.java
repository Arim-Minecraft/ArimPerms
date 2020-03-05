/* 
 * ArimPerms-sponge
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * ArimPerms-sponge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ArimPerms-sponge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ArimPerms-sponge. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.perms.sponge.hook;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.service.permission.PermissionDescription.Builder;
import org.spongepowered.api.text.Text;

public class BlankDescriptionBuilder implements Builder {
	
	private final SpongeHook hook;
	
	private String permission;
	private Text description;
	
	BlankDescriptionBuilder(SpongeHook hook) {
		this.hook = hook;
	}
	
	@Override
	public Builder id(String permissionId) {
		permission = permissionId;
		return this;
	}
	
	@Override
	public Builder description(Text description) {
		this.description = description;
		return this;
	}
	
	@Override
	public Builder assign(String role, boolean value) {
		return this;
	}
	
	@Override
	public PermissionDescription register() throws IllegalStateException {
		return new PermissionDescription() {
			
			@Override
			public String getId() {
				return permission;
			}
			
			@Override
			public Optional<Text> getDescription() {
				return Optional.ofNullable(description);
			}
			
			@Override
			public Optional<PluginContainer> getOwner() {
				return Optional.empty();
			}
			
			@Override
			public CompletableFuture<Map<SubjectReference, Boolean>> findAssignedSubjects(String collectionIdentifier) {
				return hook.getCollection(collectionIdentifier).get().getAllWithPermission(permission);
			}
			
			@Override
			public Map<Subject, Boolean> getAssignedSubjects(String collectionIdentifier) {
				return hook.getCollection(collectionIdentifier).get().getLoadedWithPermission(collectionIdentifier);
			}
			
		};
	}
	
}
