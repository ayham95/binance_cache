package com.example.binance_cache.cache

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class StackTransactionCacheTest {
    private lateinit var cache: Cache<String, String>
    private lateinit var stackTransactionalCache: TransactionalCache<String, String>

    @Before
    fun setup() {
        cache = FakeCache()
        stackTransactionalCache = StackTransactionCache(cache = cache)
    }

    @Test
    fun `put should store key value pairs and fetch a value by key`() {
        val key = "foo"
        val value = "123"

        stackTransactionalCache.put(key, value)

        val fetchedValue = stackTransactionalCache.fetch(key)
        assertEquals(fetchedValue, value)
    }

    @Test
    fun `delete should delete a pair given the key`() {
        val key = "foo"
        val value = "123"

        stackTransactionalCache.put(key, value)
        stackTransactionalCache.delete(key)
        Assert.assertNull(stackTransactionalCache.fetch(key))
    }

    @Test
    fun `count should count value occurrences`() {
        val key1 = "foo"
        val value1 = "123"
        val key2 = "bar"
        val key3 = "baz"
        val value3 = "abc"

        stackTransactionalCache.put(key1, value1)
        stackTransactionalCache.put(key2, value1)
        stackTransactionalCache.put(key3, value3)

        assertEquals(stackTransactionalCache.count(value1), 2)
        assertEquals(stackTransactionalCache.count(value3), 1)
        assertEquals(stackTransactionalCache.count(key1), 0)
    }

    @Test
    fun `begin should begin a transaction when begin is called`() {
        val key = "foo"
        val value = "123"

        stackTransactionalCache.begin()
        stackTransactionalCache.put(key, value)

        val fetchedValue = stackTransactionalCache.fetch(key)
        assertEquals(fetchedValue, value)
        Assert.assertTrue(stackTransactionalCache.hasTransaction())
    }


    @Test
    fun `commit should commit a transaction when commit is called after begin`() {
        val key = "foo"
        val value1 = "123"
        val value2 = "456"

        stackTransactionalCache.put(key, value1)
        stackTransactionalCache.begin()
        stackTransactionalCache.put(key, value2)
        stackTransactionalCache.commit()

        val fetchedValue = stackTransactionalCache.fetch(key)
        assertEquals(fetchedValue, value2)
        Assert.assertFalse(stackTransactionalCache.hasTransaction())
    }

    @Test
    fun `rollback should rollback a transaction when rollback is called after begin`() {

        val key = "foo"
        val value1 = "123"
        val value2 = "456"

        stackTransactionalCache.put(key, value1)
        stackTransactionalCache.begin()
        stackTransactionalCache.put(key, value2)
        stackTransactionalCache.rollback()

        assertEquals(value1, stackTransactionalCache.fetch(key))
        Assert.assertFalse(stackTransactionalCache.hasTransaction())
    }

    @Test
    fun `it should handle multiple transactions`() {

        val key = "foo"
        val value1 = "123"
        val value2 = "456"

        stackTransactionalCache.put(key, value1)
        stackTransactionalCache.begin()
        stackTransactionalCache.put(key, value2)
        stackTransactionalCache.begin()
        stackTransactionalCache.delete(key)
        stackTransactionalCache.rollback()
        stackTransactionalCache.commit()

        assertEquals(value2, stackTransactionalCache.fetch(key))
        Assert.assertFalse(stackTransactionalCache.hasTransaction())
    }
}