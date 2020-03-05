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

import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;

public class SimpleSubjectReference implements SubjectReference {

	private final SpongeHook hook;
	private final String collId;
	private final String subjId;
	
	SimpleSubjectReference(SpongeHook hook, SubjectCollection collection, String subjId) {
		this.hook = hook;
		collId = collection.getIdentifier();
		this.subjId = subjId;
	}
	
	@Override
	public String getCollectionIdentifier() {
		return collId;
	}
	
	@Override
	public String getSubjectIdentifier() {
		return subjId;
	}
	
	@Override
	public CompletableFuture<Subject> resolve() {
		return hook.supply(() -> hook.getCollection(getCollectionIdentifier()).get().getSubject(getSubjectIdentifier()).get());
	}

}
