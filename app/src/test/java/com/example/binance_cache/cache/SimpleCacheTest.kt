package com.example.binance_cache.cache

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SimpleCacheTest {
    private lateinit var cache: SimpleCache<String, String>

    @Before
    fun setUp() {
        cache = SimpleCache()
    }

    @Test
    fun `put should store key value pairs and fetch a value by key`() {
        val key = "foo"
        val value = "123"

        cache.put(key, value)

        assertEquals(cache.fetch(key), value)
    }

    @Test
    fun `delete should delete a pair given the key`() {
        val key = "foo"
        val value = "123"

        cache.put(key, value)
        cache.delete(key)

        assertNull(cache.fetch(key))
    }

    @Test
    fun `count should count value occurrences`() {
        val key1 = "foo"
        val value1 = "123"
        val key2 = "bar"
        val key3 = "baz"
        val value3 = "abc"

        cache.put(key1, value1)
        cache.put(key2, value1)
        cache.put(key3, value3)

        assertEquals(cache.count(value1), 2)
        assertEquals(cache.count(value3), 1)
        assertEquals(cache.count(key1), 0)
    }
}