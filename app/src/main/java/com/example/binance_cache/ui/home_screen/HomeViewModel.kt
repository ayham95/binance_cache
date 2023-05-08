package com.example.binance_cache.ui.home_screen

import androidx.lifecycle.ViewModel
import com.example.binance_cache.cache.TransactionalCache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(private val cache: TransactionalCache<String, String>) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    fun fetch() {
        val key = state.value.key
        val value = cache.fetch(key)
        val commands = state.value.commands + Command.Get(key, value)
        val newState = state.value.copy(value = "", key = "", commands = commands)
        _state.value = newState
        changeButtonsState()
    }

    fun setKeyValue() {
        val key = state.value.key
        val value = state.value.value
        if (key.isNotEmpty() && value.isNotEmpty()) {
            cache.put(key, value)
            val commands = state.value.commands + Command.Set(key, value)
            val newState = state.value.copy(value = "", key = "", commands = commands)
            _state.value = newState
        }
        changeButtonsState()
    }

    fun delete() {
        val key = state.value.key
        val commands = state.value.commands + Command.Delete(key)
        cache.delete(key)
        val newState = state.value.copy(value = "", key = "", commands = commands)
        _state.value = newState
        changeButtonsState()
    }

    fun count() {
        val value = state.value.value
        val count = cache.count(value)
        val commands = state.value.commands + Command.Count(value, count)
        val newState = state.value.copy(value = "", key = "", commands = commands)
        _state.value = newState
        changeButtonsState()
    }

    fun beginTransaction() {
        val commands = state.value.commands + Command.Begin
        cache.begin()
        val newState = state.value.copy(commands = commands)
        _state.value = newState
    }

    fun rollbackTransaction() {
        val commands = state.value.commands + Command.Rollback(success = cache.hasTransaction())
        cache.rollback()
        val newState = state.value.copy(commands = commands)
        _state.value = newState
    }

    fun commitTransaction() {
        val commands = state.value.commands + Command.Commit(success = cache.hasTransaction())
        cache.commit()
        val newState = state.value.copy(commands = commands)
        _state.value = newState
    }

    fun changeKey(key: String) {
        val newState = state.value.copy(key = key)
        _state.value = newState
        changeButtonsState()
    }

    fun changeValue(value: String) {
        val newState = state.value.copy(value = value)
        _state.value = newState
        changeButtonsState()
    }

    private fun changeButtonsState() {
        val key = state.value.key
        val value = state.value.value
        var getButtonEnabled = false
        var setButtonEnabled = false
        var deleteButtonEnabled = false
        var countButtonEnabled = false
        if (key.isNotEmpty() && value.isNotEmpty()) {
            setButtonEnabled = true
        }
        if (key.isNotEmpty()) {
            getButtonEnabled = true
            deleteButtonEnabled = true
        }
        if (value.isNotEmpty()) {
            countButtonEnabled = true
        }
        _state.value = state.value.copy(
            isGetEnabled = getButtonEnabled,
            isSetEnabled = setButtonEnabled,
            isCountEnabled = countButtonEnabled,
            isDeleteEnabled = deleteButtonEnabled,
        )
    }
}