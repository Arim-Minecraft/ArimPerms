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
import java.util.Iterator;
import java.util.Set;

import org.spongepowered.api.service.context.Context;

import space.arim.api.util.collect.helper.SetToArrayHelper;
import space.arim.api.util.collect.helper.UnmodifiableByDefaultSet;

public class AbstractContextsMapKeySet<T> implements UnmodifiableByDefaultSet<Set<Context>>, SetToArrayHelper<Set<Context>> {
	
	private final AbstractContextsMap<T> map;
	
	public AbstractContextsMapKeySet(AbstractContextsMap<T> map) {
		this.map = map;
	}
	
	@Override
	public int size() {
		return map.size();
	}
	
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	@Override
	public boolean contains(Object o) {
		return true;
	}
	
	@Override
	public Iterator<Set<Context>> iterator() {
		return new Iterator<Set<Context>>() {
			
			private final Iterator<Context> it = map.getWorldContextsStream().iterator();
			
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}
			
			@Override
			public Set<Context> next() {
				return Collections.singleton(it.next());
			}
			
		};
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return true;
	}

}
