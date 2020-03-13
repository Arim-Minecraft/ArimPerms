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
import java.util.stream.Stream;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.context.Context;

import space.arim.api.util.collect.CalculationMapEntrySet;
import space.arim.api.util.collect.CalculationMapValues;
import space.arim.api.util.collect.helper.UnmodifiableByDefaultMap;

public abstract class AbstractContextsMap<T> implements UnmodifiableByDefaultMap<Set<Context>, T> {
	
	private final AbstractContextsMapKeySet<T> keySet = new AbstractContextsMapKeySet<T>(this);
	private volatile CalculationMapValues<Set<Context>, T> values;
	private volatile Set<Entry<Set<Context>, T>> entrySet;
	
	private Stream<String> getWorldStream() {
		return Sponge.getServer().getWorlds().stream().map((world) -> world.getName());
	}
	
	Stream<Context> getWorldContextsStream() {
		return getWorldStream().map((world) -> new Context(Context.WORLD_KEY, world));
	}
	
	@Override
	public int size() {
		return (int) getWorldStream().count();
	}
	
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public boolean containsKey(Object key) {
		return keySet().contains(key);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public boolean containsValue(Object value) {
		return values().contains(value);
	}
	
	@Override
	public Set<Set<Context>> keySet() {
		return keySet;
	}
	
	@Override
	public Collection<T> values() {
		return (values != null) ? values : (values = new CalculationMapValues<Set<Context>, T>(this));
	}
	
	protected Set<Entry<Set<Context>, T>> instantiateEntrySet() {
		return new CalculationMapEntrySet<Set<Context>, T>(this);
	}
	
	@Override
	public Set<Entry<Set<Context>, T>> entrySet() {
		return (entrySet != null) ? entrySet : (entrySet = instantiateEntrySet());
	}
	
}
