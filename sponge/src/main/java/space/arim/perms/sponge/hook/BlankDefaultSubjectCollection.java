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
import java.util.Collections;
import java.util.Set;

import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;

public class BlankDefaultSubjectCollection extends AbstractSubjectCollection {

	private final BlankDefaultSubject blankSubject;
	
	BlankDefaultSubjectCollection(SpongeHook hook) {
		super(hook, PermissionService.SUBJECTS_DEFAULT);
		blankSubject = new BlankDefaultSubject(hook, this);
	}
	
	BlankDefaultSubject getSubject() {
		return blankSubject;
	}
	
	@Override
	public Collection<Subject> getLoadedSubjects() {
		return Collections.singleton(blankSubject);
	}
	
	@Override
	Set<String> getIdentifiers() {
		return Collections.singleton(blankSubject.getIdentifier());
	}
	
	@Override
	boolean isValid(String identifier) {
		return identifier.equals(blankSubject.getIdentifier());
	}

}
