package com.example.binance_cache

import androidx.lifecycle.ViewModel
import com.example.binance_cache.cache.SimpleCache
import com.example.binance_cache.cache.StackTransactionCache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val cache = StackTransactionCache<String, String>(SimpleCache())
    private var _state = MutableStateFlow(HomeState(commands = emptyList(), key = "", value = ""))
    val state = _state.asStateFlow()

    fun fetch() {
        val key = state.value.key
        val value = cache.fetch(key)
        var commands = state.value.commands + "> GET $key"
        commands = if (value != null) {
            commands + value
        } else {
            commands + "Key not set"
        }

        val newState = state.value.copy(value = "", key = "", commands = commands)
        _state.value = newState
    }

    fun setKeyValue() {
        val key = state.value.key
        val value = state.value.value
        val commands = state.value.commands + "> SET $key $value"
        if (key.isNotEmpty() && value.isNotEmpty()) {
            cache.put(key, value)
        }
        val newState = state.value.copy(value = "", key = "", commands = commands)
        _state.value = newState
    }

    fun delete() {
        val key = state.value.key
        var commands = state.value.commands + "> DELETE $key"
        val deleted = cache.delete(key) != null
        if (!deleted) {
            commands = state.value.commands + "> DELETE $key"
        }

        val newState = state.value.copy(value = "", key = "", commands = commands)
        _state.value = newState
    }

    fun count() {
        val value = state.value.value
        var commands = state.value.commands + "> COUNT $value"
        val count = cache.count(value)
        commands = commands + "$count"
        val newState = state.value.copy(value = "", key = "", commands = commands)
        _state.value = newState
    }

    fun beginTransaction() {
        val commands = state.value.commands + "> BEGIN"
        cache.begin()
        val newState = state.value.copy(commands = commands)
        _state.value = newState
    }

    fun rollbackTransaction() {
        val commands = state.value.commands + "> ROLLBACK"
        cache.rollback()
        val newState = state.value.copy(commands = commands)
        _state.value = newState
    }

    fun commitTransaction() {
        val commands = state.value.commands + "> COMMIT"
        cache.commit()
        val newState = state.value.copy(commands = commands)
        _state.value = newState
    }

    fun changeKey(key: String) {
        val newState = state.value.copy(key = key)
        _state.value = newState
    }

    fun changeValue(value: String) {
        val newState = state.value.copy(value = value)
        _state.value = newState
    }
}