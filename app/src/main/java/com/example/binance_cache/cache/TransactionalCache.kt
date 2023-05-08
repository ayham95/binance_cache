package com.example.binance_cache.cache

interface TransactionalCache<K, V> {
    fun put(key: K, value: V): V?
    fun fetch(key: K): V?
    fun delete(key: K): V?
    fun count(value: V): Int
    fun begin()
    fun rollback()
    fun commit()
    fun hasTransaction(): Boolean
}