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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionDescription.Builder;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;

import space.arim.universal.util.concurrent.CompetitiveFuture;

import space.arim.api.concurrent.AsyncExecution;

import space.arim.perms.api.ArimPerms;

public class SpongeHook implements PermissionService {

	final ArimPerms core;
	private final Set<ContextCalculator<Subject>> calculators = ConcurrentHashMap.newKeySet();
	
	private final BlankDefaultSubjectCollection blankColl;
	private final GroupsCollection groupsColl;
	private final UsersCollection usersColl;
	private final Map<String, SubjectCollection> collMap;
	private final Set<String> allIdentifiers;
	
	public SpongeHook(ArimPerms core) {
		this.core = core;
		blankColl = new BlankDefaultSubjectCollection(this);
		groupsColl = new GroupsCollection(this);
		usersColl = new UsersCollection(this);
		Map<String, SubjectCollection> collMap = new HashMap<String, SubjectCollection>();
		collMap.put(SUBJECTS_GROUP, groupsColl);
		collMap.put(SUBJECTS_USER, usersColl);
		this.collMap = Collections.unmodifiableMap(collMap);
		allIdentifiers = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(blankColl.getIdentifier(), groupsColl.getIdentifier(), usersColl.getIdentifier())));
	}
	
	AsyncExecution getExecutor() {
		return core.getRegistry().getRegistration(AsyncExecution.class);
	}
	
	<T> CompletableFuture<T> completed(T value) {
		return CompetitiveFuture.completed(value, getExecutor());
	}
	
	<T> CompletableFuture<T> supply(Supplier<T> supplier) {
		return CompetitiveFuture.supplyAsync(supplier, getExecutor());
	}
	
	@Override
	public void registerContextCalculator(ContextCalculator<Subject> calculator) {
		calculators.add(calculator);
	}
	
	@Override
	public GroupsCollection getGroupSubjects() {
		return groupsColl;
	}
	
	@Override
	public UsersCollection getUserSubjects() {
		return usersColl;
	}
	
	@Override
	public CompletableFuture<Set<String>> getAllIdentifiers() {
		return completed(allIdentifiers);
	}
	
	@Override
	public Map<String, SubjectCollection> getLoadedCollections() {
		return collMap;
	}
	
	@Override
	public Optional<SubjectCollection> getCollection(String identifier) {
		switch (identifier) {
		case SUBJECTS_DEFAULT:
			return Optional.of(blankColl);
		case SUBJECTS_GROUP:
			return Optional.of(groupsColl);
		case SUBJECTS_USER:
			return Optional.of(usersColl);
		default:
			return Optional.empty();
		}
	}
	
	@Override
	public Subject getDefaults() {
		return blankColl.getSubject();
	}
	
	@Override
	public Optional<PermissionDescription> getDescription(String permission) {
		return Optional.empty();
	}
	
	@Override
	public Collection<PermissionDescription> getDescriptions() {
		return Collections.emptySet();
	}
	
	@Override
	public Predicate<String> getIdentifierValidityPredicate() {
		return (id) -> getCollection(id).isPresent();
	}
	
	@Override
	public CompletableFuture<Boolean> hasCollection(String identifier) {
		return completed(getCollection(identifier).isPresent());
	}
	
	@Override
	public CompletableFuture<SubjectCollection> loadCollection(String identifier) {
		return supply(() -> getCollection(identifier).get());
	}
	
	@Override
	public Builder newDescriptionBuilder(Object plugin) {
		return new BlankDescriptionBuilder(this);
	}
	
	SubjectReference getSubjectReference(String collId, String subjId) {
		return new SimpleSubjectReference(this, collId, subjId);
	}
	
	@Override
	public SubjectReference newSubjectReference(String collectionIdentifier, String subjectIdentifier) {
		return getSubjectReference(collectionIdentifier, subjectIdentifier);
	}
	
}
