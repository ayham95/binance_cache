package com.example.binance_cache.ui.home_screen

data class HomeState(
    val key: String = "",
    val value: String = "",
    val commands: List<Command> = emptyList(),
    val isGetEnabled: Boolean = false,
    val isSetEnabled: Boolean = false,
    val isDeleteEnabled: Boolean = false,
    val isCountEnabled: Boolean = false,
)
