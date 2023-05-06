package com.example.binance_cache.cache

interface Cache<K, V> {
    fun put(key: K, value: V): V?
    fun fetch(key: K): V?
    fun delete(key: K): V?
    fun count(value: V): Int
}