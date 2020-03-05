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

import java.util.Optional;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;

public abstract class AbstractSubject implements Subject {
	
	final SpongeHook hook;
	private final SubjectCollection collection;
	private final SubjectReference reference;
	
	AbstractSubject(SpongeHook hook, SubjectCollection collection) {
		this.hook = hook;
		this.collection = collection;
		reference = hook.getSubjectReference(getContainingCollection().getIdentifier(), getIdentifier());
	}
	
	@Override
	public SubjectReference asSubjectReference() {
		return reference;
	}
	
	@Override
	public Optional<CommandSource> getCommandSource() {
		return Optional.empty();
	}
	
	@Override
	public SubjectCollection getContainingCollection() {
		return collection;
	}
	
	@Override
	public boolean isSubjectDataPersisted() {
		return true;
	}

}
