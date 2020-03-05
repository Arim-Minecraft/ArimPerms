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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;

public abstract class AbstractSubjectCollection implements SubjectCollection {

	final SpongeHook hook;
	private final String id;
	
	AbstractSubjectCollection(SpongeHook hook, String id) {
		this.hook = hook;
		this.id = id;
	}
	
	private <T> Map<T, Boolean> collectToBooleanMap(Stream<T> objects, boolean trueOrFalse) {
		return objects.collect(Collectors.toMap(Function.identity(), (subject) -> trueOrFalse, (b1, b2) -> b1, () -> new HashMap<T, Boolean>()));
	}
	
	Map<Subject, Boolean> allSubjWithPerm(Predicate<Subject> checker) {
		return collectToBooleanMap(getLoadedSubjects().stream().filter(checker), true);
	}
	
	Map<SubjectReference, Boolean> allRefWithPerm(Predicate<Subject> checker) {
		return collectToBooleanMap(getLoadedSubjects().stream().filter(checker).map((subject) -> subject.asSubjectReference()), true);
	}
	
	CompletableFuture<Map<Subject, Boolean>> allSubjWithPermAsync(Predicate<Subject> checker) {
		return hook.supply(() -> allSubjWithPerm(checker));
	}
	
	CompletableFuture<Map<SubjectReference, Boolean>> allRefWithPermAsync(Predicate<Subject> checker) {
		return hook.supply(() -> allRefWithPerm(checker));
	}
	
	@Override
	public String getIdentifier() {
		return id;
	}
	
	abstract Set<String> getIdentifiers();
	
	@Override
	public CompletableFuture<Set<String>> getAllIdentifiers() {
		return hook.supply(() -> getIdentifiers());
	}
	
	@Override
	public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(String permission) {
		return allRefWithPermAsync((subject) -> subject.hasPermission(permission));
	}
	
	@Override
	public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(Set<Context> contexts, String permission) {
		return allRefWithPermAsync((subject) -> subject.hasPermission(contexts, permission));
	}
	
	@Override
	public Subject getDefaults() {
		return hook.getDefaults();
	}
	
	abstract boolean isValid(String identifier);
	
	@Override
	public Predicate<String> getIdentifierValidityPredicate() {
		return this::isValid;
	}
	
	@Override
	public Map<Subject, Boolean> getLoadedWithPermission(String permission) {
		return allSubjWithPerm((subject) -> subject.hasPermission(permission));
	}
	
	@Override
	public Map<Subject, Boolean> getLoadedWithPermission(Set<Context> contexts, String permission) {
		return allSubjWithPerm((subject) -> subject.hasPermission(contexts, permission));
	}
	
	@Override
	public Optional<Subject> getSubject(String identifier) {
		return getLoadedSubjects().stream().filter((subject) -> subject.getIdentifier().equals(identifier)).findAny();
	}
	
	@Override
	public CompletableFuture<Boolean> hasSubject(String identifier) {
		return hook.supply(() -> getLoadedSubjects().stream().anyMatch((subject) -> subject.getIdentifier().equalsIgnoreCase(identifier)));
	}
	
	@Override
	public CompletableFuture<Subject> loadSubject(String identifier) {
		return hook.supply(() -> getSubject(identifier).get());
	}
	
	@Override
	public CompletableFuture<Map<String, Subject>> loadSubjects(Set<String> identifiers) {
		return hook.supply(() -> {
			Map<String, Subject> subjects = new HashMap<String, Subject>();
			for (Subject subject : getLoadedSubjects()) {
				for (String id : identifiers) {
					if (subject.getIdentifier().equals(id)) {
						subjects.put(id, subject);
						break;
					}
				}
			}
			return subjects;
		});
	}
	
	@Override
	public SubjectReference newSubjectReference(String subjectIdentifier) {
		return new SimpleSubjectReference(hook, this, subjectIdentifier);
	}
	
	@Override
	public void suggestUnload(String identifier) {
		
	}

}
