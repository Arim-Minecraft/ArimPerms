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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

public class SubjData implements SubjectData {

	private final SpongeUser user;
	
	SubjData(SpongeUser user) {
		this.user = user;
	}
	
	@Override
	public CompletableFuture<Boolean> addParent(Set<Context> contexts, SubjectReference parent) {
		return null;
	}

	@Override
	public CompletableFuture<Boolean> clearOptions() {
		return null;
	}

	@Override
	public CompletableFuture<Boolean> clearOptions(Set<Context> contexts) {
		return null;
	}

	@Override
	public CompletableFuture<Boolean> clearParents() {
		return null;
	}

	@Override
	public CompletableFuture<Boolean> clearParents(Set<Context> contexts) {
		return null;
	}

	@Override
	public CompletableFuture<Boolean> clearPermissions() {
		return null;
	}

	@Override
	public CompletableFuture<Boolean> clearPermissions(Set<Context> contexts) {
		return null;
	}

	@Override
	public Map<Set<Context>, Map<String, String>> getAllOptions() {
		return null;
	}

	@Override
	public Map<Set<Context>, List<SubjectReference>> getAllParents() {
		return null;
	}

	@Override
	public Map<Set<Context>, Map<String, Boolean>> getAllPermissions() {
		return null;
	}

	@Override
	public Map<String, String> getOptions(Set<Context> contexts) {
		return null;
	}

	@Override
	public List<SubjectReference> getParents(Set<Context> contexts) {
		return null;
	}

	@Override
	public Map<String, Boolean> getPermissions(Set<Context> contexts) {
		return null;
	}
	
	@Override
	public CompletableFuture<Boolean> removeParent(Set<Context> contexts, SubjectReference parent) {
		return null;
	}
	
	@Override
	public CompletableFuture<Boolean> setOption(Set<Context> contexts, String key, String value) {
		return null;
	}
	
	@Override
	public CompletableFuture<Boolean> setPermission(Set<Context> contexts, String permission, Tristate value) {
		return null;
	}
	
}
