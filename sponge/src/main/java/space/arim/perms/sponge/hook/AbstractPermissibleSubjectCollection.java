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

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.spongepowered.api.service.permission.Subject;
import space.arim.perms.api.Permissible;

public abstract class AbstractPermissibleSubjectCollection<T extends Permissible> extends AbstractSubjectCollection {
	
	private final ConcurrentHashMap<T, Subject> cache = new ConcurrentHashMap<T, Subject>();
	
	AbstractPermissibleSubjectCollection(SpongeHook hook, String id) {
		super(hook, id);
	}
	
	abstract Collection<T> getPermissibles();
	
	abstract Subject convertFresh(T permissible);
	
	private Subject convert(T permissible) {
		return cache.computeIfAbsent(permissible, this::convertFresh);
	}
	
	@Override
	public Collection<Subject> getLoadedSubjects() {
		return getPermissibles().stream().map(this::convert).collect(Collectors.toSet());
	}
	
	@Override
	Set<String> getIdentifiers() {
		return getPermissibles().stream().map((permissible) -> permissible.getId()).collect(Collectors.toSet());
	}
	
	@Override
	boolean isValid(String identifier) {
		return getPermissibles().stream().anyMatch((permissible) -> permissible.getId().equals(identifier));
	}

}
