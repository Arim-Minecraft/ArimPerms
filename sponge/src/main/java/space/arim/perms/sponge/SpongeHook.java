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
package space.arim.perms.sponge;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;

import space.arim.universal.util.concurrent.CompetitiveFuture;

import space.arim.api.concurrent.AsyncExecution;

import space.arim.perms.api.ArimPerms;

import org.spongepowered.api.service.permission.PermissionDescription.Builder;
import org.spongepowered.api.service.permission.PermissionService;

public class SpongeHook implements PermissionService {

	private final ArimPerms core;
	
	public SpongeHook(ArimPerms core) {
		this.core = core;
	}
	
	private AsyncExecution getExecutor() {
		return core.getRegistry().getRegistration(AsyncExecution.class);
	}
	
	@Override
	public void registerContextCalculator(ContextCalculator<Subject> calculator) {
		
	}
	
	@Override
	public CompletableFuture<Set<String>> getAllIdentifiers() {
		return null;
	}
	
	@Override
	public Optional<SubjectCollection> getCollection(String identifier) {
		return null;
	}
	
	@Override
	public Subject getDefaults() {
		return null;
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
	public SubjectCollection getGroupSubjects() {
		return null;
	}
	
	@Override
	public Predicate<String> getIdentifierValidityPredicate() {
		return null;
	}
	
	@Override
	public Map<String, SubjectCollection> getLoadedCollections() {
		return null;
	}
	
	@Override
	public SubjectCollection getUserSubjects() {
		return null;
	}
	
	@Override
	public CompletableFuture<Boolean> hasCollection(String identifier) {
		return CompetitiveFuture.completed(core.users().getPossibleUser(identifier) != null || core.groups().getPossibleGroup(identifier) != null, getExecutor());
	}
	
	@Override
	public CompletableFuture<SubjectCollection> loadCollection(String identifier) {
		return null;
	}
	
	@Override
	public Builder newDescriptionBuilder(Object plugin) {
		return null;
	}
	
	@Override
	public SubjectReference newSubjectReference(String collectionIdentifier, String subjectIdentifier) {
		return null;
	}
	
}
