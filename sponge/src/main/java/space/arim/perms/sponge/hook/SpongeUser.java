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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import space.arim.perms.api.User;

public class SpongeUser implements Subject {

	private final SpongeHook hook;
	private final User user;
	private final SubjectCollection collection;
	private final SubjectReference reference;
	
	SpongeUser(SpongeHook hook, User user, SubjectCollection collection) {
		this.hook = hook;
		this.user = user;
		this.collection = collection;
		reference = new SubjRef(hook, collection, this);
	}
	
	@Override
	public Set<Context> getActiveContexts() {
		return Collections.emptySet();
	}
	
	@Override
	public String getIdentifier() {
		return user.getId();
	}
	
	@Override
	public SubjectReference asSubjectReference() {
		return reference;
	}
	
	@Override
	public Optional<CommandSource> getCommandSource() {
		for (Player player : Sponge.getServer().getOnlinePlayers()) {
			if (player.getUniqueId().toString().replace("-", "").equals(user.getId())) {
				return Optional.of(player);
			}
		}
		return Optional.empty();
	}
	
	@Override
	public SubjectCollection getContainingCollection() {
		return collection;
	}
	
	@Override
	public Optional<String> getOption(Set<Context> contexts, String key) {
		return null;
	}
	
	@Override
	public List<SubjectReference> getParents(Set<Context> contexts) {
		return null;
	}
	
	@Override
	public Tristate getPermissionValue(Set<Context> contexts, String permission) {
		return null;
	}
	
	@Override
	public SubjectData getSubjectData() {
		return null;
	}
	
	@Override
	public SubjectData getTransientSubjectData() {
		return null;
	}
	
	@Override
	public boolean isChildOf(Set<Context> contexts, SubjectReference parent) {
		return false;
	}
	
	@Override
	public boolean isSubjectDataPersisted() {
		return true;
	}
	
}
