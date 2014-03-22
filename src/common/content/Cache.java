package common.content;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic cache that accepts up to three Object cache keys.
 * @author Andrew
 * @param <V> Type of data to be cached
 */
public class Cache<V> {

	private final Map<Integer, V> cache;

	public Cache() {
		cache = new HashMap<>();
	}

	public V get(Object key1){
		return get(key1, null, null);
	}

	public V get(Object key1, Object key2){
		return get(key1, key2, null);
	}

	public V get(Object key1, Object key2, Object key3){
		int hash = hash(key1, key2, key3);
		return cache.get(hash);
	}

	public void put(Object key1, V value){
		put(key1, null, value);
	}

	public void put(Object key1, Object key2, V value){
		put(key1, key2, null, value);
	}

	public void put(Object key1, Object key2, Object key3, V value){
		int hash = hash(key1, key2, key3);
		cache.put(hash, value);
	}

	private int hash(Object key1, Object key2, Object key3){
		int hash = 8575906;		
		hash += (key1 != null ? key1.hashCode() : 0) * 12;
		hash += (key2 != null ? key2.hashCode() : 0) * 35;
		hash += (key3 != null ? key3.hashCode() : 0) * 54;
		return hash;
	}

}
