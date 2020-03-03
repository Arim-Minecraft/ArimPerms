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
import org.spongepowered.api.text.Text;

public class SpongePermBuilder implements PermissionDescription {

	@Override
	public CompletableFuture<Map<SubjectReference, Boolean>> findAssignedSubjects(String collectionIdentifier) {
		return null;
	}
	
	@Override
	public Map<Subject, Boolean> getAssignedSubjects(String collectionIdentifier) {
		return null;
	}
	
	@Override
	public Optional<Text> getDescription() {
		return Optional.empty();
	}
	
	@Override
	public String getId() {
		return null;
	}
	
	@Override
	public Optional<PluginContainer> getOwner() {
		return null;
	}
	
	static class Builder implements PermissionDescription.Builder {
		
		@Override
		public PermissionDescription.Builder assign(String role,
				boolean value) {
			return null;
		}
		
		@Override
		public PermissionDescription.Builder description(Text arg0) {
			return null;
		}
		
		@Override
		public PermissionDescription.Builder id(String permissionId) {
			return null;
		}
		
		@Override
		public PermissionDescription register() throws IllegalStateException {
			return null;
		}
		
	}
	
}
