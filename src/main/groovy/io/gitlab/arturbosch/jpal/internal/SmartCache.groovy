package io.gitlab.arturbosch.jpal.internal

import groovy.transform.CompileStatic

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * @author Artur
 */
@CompileStatic
final class SmartCache<K, V> {

	private final ConcurrentMap<K, V> cache = new ConcurrentHashMap<>()

	private final V defaultValue

	SmartCache() {
		this.defaultValue = null
	}

	void reset() {
		cache.clear()
	}

	Optional<V> get(K key) {
		return Optional.ofNullable(cache.get(key))
	}

	void put(K key, V value) {
		Validate.notNull(key, "Key must not be null!")
		Validate.notNull(value, "Value must not be null!")
		cache.put(key, value)
	}

	V remove(K key) {
		cache.remove(key)
	}

	Set<K> keys() {
		cache.keySet()
	}

	Set<V> values() {
		cache.values().toSet()
	}

}
