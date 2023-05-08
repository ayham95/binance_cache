package com.example.binance_cache.di

import com.example.binance_cache.cache.SimpleCache
import com.example.binance_cache.cache.StackTransactionCache
import com.example.binance_cache.ui.home_screen.HomeViewModel

object HomeContainer {
    private val simpleCache by lazy { SimpleCache<String, String>() }
    private val cache by lazy { StackTransactionCache(simpleCache) }
    val homeViewModelFactory = ViewModelFactory(HomeViewModel(cache))
}