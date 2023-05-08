package com.example.binance_cache.ui.home_screen

sealed class Command {
    data class Get(val key: String, val output: String?) : Command()
    data class Set(val key: String, val value: String) : Command()
    data class Delete(val key: String) : Command()
    data class Count(val value: String, val output: Int?) : Command()
    object Begin : Command()
    data class Commit(val success : Boolean = true) : Command()
    data class Rollback(val success: Boolean = true) : Command()
}
