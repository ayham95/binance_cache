package com.example.binance_cache.cache

sealed class TransactionOperation<K, V> {
    class Begin<K, V> : TransactionOperation<K, V>()

    data class Put<K, V>(val key: K, val value: V, val originalValue: V?) :
        TransactionOperation<K, V>()

    data class Delete<K, V>(val key: K, val originalValue: V?) : TransactionOperation<K, V>()

}