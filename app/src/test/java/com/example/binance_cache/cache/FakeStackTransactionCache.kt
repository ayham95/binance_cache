package com.example.binance_cache.cache

class FakeStackTransactionCache : TransactionalCache<String, String> {
    private val map = mutableMapOf<String, String>()

    var numberOfTransactions = 0

    override fun put(key: String, value: String): String? = map.put(key, value)

    override fun fetch(key: String): String? = map[key]

    override fun delete(key: String): String? = map.remove(key)

    override fun count(value: String): Int = map.count { it.value == value }

    override fun begin() {
        numberOfTransactions++
    }

    override fun rollback() {
        if(numberOfTransactions > 0) {
            numberOfTransactions--
        }
    }

    override fun commit() {
        if(numberOfTransactions > 0) {
            numberOfTransactions--
        }
    }

    override fun hasTransaction(): Boolean = numberOfTransactions > 0
}