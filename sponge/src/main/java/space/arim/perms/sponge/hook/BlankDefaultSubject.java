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

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

public class BlankDefaultSubject extends AbstractSubject {
	
	private final BlankDefaultSubjectData blankData;
	
	BlankDefaultSubject(SpongeHook hook, SubjectCollection collection) {
		super(hook, collection);
		blankData = new BlankDefaultSubjectData(hook);
	}
	
	@Override
	public Set<Context> getActiveContexts() {
		return Collections.emptySet();
	}
	
	@Override
	public String getIdentifier() {
		return "default";
	}
	
	@Override
	public Optional<String> getOption(Set<Context> contexts, String key) {
		return Optional.empty();
	}
	
	@Override
	public List<SubjectReference> getParents(Set<Context> contexts) {
		return Collections.emptyList();
	}
	
	@Override
	public Tristate getPermissionValue(Set<Context> contexts, String permission) {
		return Tristate.FALSE;
	}
	
	@Override
	public SubjectData getSubjectData() {
		return blankData;
	}
	
	@Override
	public SubjectData getTransientSubjectData() {
		return blankData;
	}
	
	@Override
	public boolean isChildOf(Set<Context> contexts, SubjectReference parent) {
		return false;
	}

}
