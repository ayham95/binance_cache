package com.example.binance_cache.cache

class CountOptimizedCache<K, V> : Cache<K, V> {

    private val store = HashMap<K, V>()
    private val valueCounter = HashMap<V, Int>()

    override fun put(key: K, value: V): V? {
        val previousValue = store.put(key, value)
        incrementCount(value)
        previousValue?.let {
            decrementCount(previousValue)
        }
        return previousValue
    }

    override fun fetch(key: K): V? = store[key]

    override fun delete(key: K): V? {
        val previousValue = store.remove(key)
        previousValue?.let {
            decrementCount(previousValue)
        }
        return previousValue
    }

    override fun count(value: V): Int = valueCounter[value] ?: 0

    private fun incrementCount(value: V) {
        var valueCount = valueCounter.getOrDefault(value, 0)
        valueCounter[value] = ++valueCount
    }

    private fun decrementCount(value: V) {
        var valueCount = valueCounter.getOrDefault(value, 0)
        if (valueCount > 0) {
            valueCounter[value] = --valueCount
        }
    }


}