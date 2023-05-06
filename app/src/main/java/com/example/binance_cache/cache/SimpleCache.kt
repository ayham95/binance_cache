package com.example.binance_cache.cache

class SimpleCache<K, V> : Cache<K, V> {

    private val store = HashMap<K, V>()

    override fun put(key: K, value: V) = store.put(key, value)

    override fun fetch(key: K) = store[key]

    override fun delete(key: K) = store.remove(key)

    override fun count(value: V) = store.count { it.value == value }
}