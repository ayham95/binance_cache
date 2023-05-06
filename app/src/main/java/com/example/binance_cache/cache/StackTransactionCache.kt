package com.example.binance_cache.cache

import java.util.Stack

class StackTransactionCache<K, V>(private val cache: Cache<K, V>) : TransactionalCache<K, V> {
    private var transactionStack = Stack<TransactionOperation<K, V>>()

    override fun put(key: K, value: V): V? {
        val originalValue = cache.put(key, value)
        if (hasTransactions) {
            transactionStack.push(TransactionOperation.Put(key, value, originalValue))
        }
        return originalValue
    }

    override fun fetch(key: K): V? = cache.fetch(key)

    override fun delete(key: K): V? {
        val originalValue = cache.delete(key)
        if (hasTransactions && originalValue != null) {
            transactionStack.push(TransactionOperation.Delete(key, originalValue))
        }
        return originalValue
    }

    override fun count(value: V) = cache.count(value)

    override fun begin() {
        transactionStack.push(TransactionOperation.Begin())
    }

    override fun rollback() {
        if (!hasTransactions) return

        while (transactionStack.peek() !is TransactionOperation.Begin<K, V>) {
            val operation = transactionStack.pop()
            if (operation is TransactionOperation.Put) {
                rollbackPut(operation)
            } else if (operation is TransactionOperation.Delete) {
                rollbackDelete(operation)
            }
        }
        transactionStack.pop()
    }

    override fun commit() {
        if (!hasTransactions) return

        while (transactionStack.peek() !is TransactionOperation.Begin<K, V>) {
            transactionStack.pop()
        }
        transactionStack.pop()
    }

    private fun rollbackPut(operation: TransactionOperation.Put<K, V>) {
        if (operation.originalValue != null) {
            cache.put(operation.key, operation.originalValue)
        } else {
            cache.delete(operation.key)
        }
    }

    private fun rollbackDelete(operation: TransactionOperation.Delete<K, V>) {
        if (operation.originalValue != null) {
            cache.put(operation.key, operation.originalValue)
        }
    }

    private val hasTransactions: Boolean
        get() = transactionStack.isNotEmpty()
}