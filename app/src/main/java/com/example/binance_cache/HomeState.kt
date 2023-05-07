package com.example.binance_cache

data class HomeState(
    val key: String,
    val value: String,
    val commands: List<String>,
)
