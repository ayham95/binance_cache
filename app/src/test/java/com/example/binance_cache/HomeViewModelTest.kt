package com.example.binance_cache

import com.example.binance_cache.cache.FakeStackTransactionCache
import com.example.binance_cache.ui.home_screen.Command
import com.example.binance_cache.ui.home_screen.HomeState
import com.example.binance_cache.ui.home_screen.HomeViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {
    private lateinit var viewModel: HomeViewModel
    private lateinit var cache: FakeStackTransactionCache

    @Before
    fun setup() {
        cache = FakeStackTransactionCache()
        cache.put("foo", "123")
        cache.put("bar", "abc")
        cache.put("baz", "!@#")
        viewModel = HomeViewModel(cache)
    }


    @Test
    fun `changeKey should change key and enable get and delete buttons`() {
        viewModel.changeKey("foo")

        val expectedState = HomeState(
            key = "foo",
            value = "",
            commands = emptyList(),
            isGetEnabled = true,
            isSetEnabled = false,
            isCountEnabled = false,
            isDeleteEnabled = true
        )
        assertEquals(expectedState, viewModel.state.value)
    }


    @Test
    fun `changeValue should change value and enable count button only`() {
        viewModel.changeValue("123")

        val expectedState = HomeState(
            key = "",
            value = "123",
            commands = emptyList(),
            isGetEnabled = false,
            isSetEnabled = false,
            isCountEnabled = true,
            isDeleteEnabled = false
        )
        assertEquals(expectedState, viewModel.state.value)
    }

    @Test
    fun `changeKey and changeValue should change key and value and enable all buttons`() {
        viewModel.changeKey("foo")
        viewModel.changeValue("123")

        val expectedState = HomeState(
            key = "foo",
            value = "123",
            commands = emptyList(),
            isGetEnabled = true,
            isSetEnabled = true,
            isCountEnabled = true,
            isDeleteEnabled = true
        )
        assertEquals(expectedState, viewModel.state.value)
    }

    @Test
    fun `fetch should update state with value from cache, reset key-value and disable buttons`() {
        viewModel.changeKey("foo")
        viewModel.fetch()

        val expectedState = HomeState(
            key = "",
            value = "",
            commands = listOf(Command.Get("foo", "123")),
            isGetEnabled = false,
            isSetEnabled = false,
            isCountEnabled = false,
            isDeleteEnabled = false
        )
        assertEquals(expectedState, viewModel.state.value)
    }

    @Test
    fun `delete should remove key-value pair from cache, reset key-value and disable buttons`() {
        viewModel.changeKey("foo")
        viewModel.delete()

        val expectedState = HomeState(
            key = "",
            value = "",
            commands = listOf(Command.Delete("foo")),
            isGetEnabled = false,
            isSetEnabled = false,
            isCountEnabled = false,
            isDeleteEnabled = false
        )
        assertEquals(expectedState, viewModel.state.value)
        assertNull(cache.fetch("foo"))
    }

    @Test
    fun `count should update state with count of value in cache, reset key-value and disable button`() {
        viewModel.changeValue("123")
        viewModel.count()

        val expectedState = HomeState(
            key = "",
            value = "",
            commands = listOf(Command.Count("123", 1)),
            isGetEnabled = false,
            isSetEnabled = false,
            isCountEnabled = false,
            isDeleteEnabled = false
        )
        assertEquals(expectedState, viewModel.state.value)
    }

    @Test
    fun `beginTransaction should call begin on cache and update state`() {
        viewModel.beginTransaction()

        val expectedState = HomeState(
            key = "",
            value = "",
            commands = listOf(Command.Begin),
            isGetEnabled = false,
            isSetEnabled = false,
            isCountEnabled = false,
            isDeleteEnabled = false
        )
        assertEquals(expectedState, viewModel.state.value)
        assertTrue(cache.hasTransaction())
    }

    @Test
    fun `rollbackTransaction should call rollback on cache and update state`() {
        viewModel.beginTransaction()
        viewModel.rollbackTransaction()

        val expectedState = HomeState(
            key = "",
            value = "",
            commands = listOf(Command.Begin, Command.Rollback(success = true)),
            isGetEnabled = false,
            isSetEnabled = false,
            isCountEnabled = false,
            isDeleteEnabled = false
        )
        assertEquals(expectedState, viewModel.state.value)
        assertFalse(cache.hasTransaction())
    }

    @Test
    fun `commit should call commit on cache and update state`() {
        viewModel.beginTransaction()
        viewModel.commitTransaction()

        val expectedState = HomeState(
            key = "",
            value = "",
            commands = listOf(Command.Begin, Command.Commit(success = true)),
            isGetEnabled = false,
            isSetEnabled = false,
            isCountEnabled = false,
            isDeleteEnabled = false
        )
        assertEquals(expectedState, viewModel.state.value)
        assertFalse(cache.hasTransaction())
    }

    @Test
    fun `rollbackTransaction should call rollback on cache and update state with error if no transactions`() {
        viewModel.rollbackTransaction()

        val expectedState = HomeState(
            key = "",
            value = "",
            commands = listOf(Command.Rollback(success = false)),
            isGetEnabled = false,
            isSetEnabled = false,
            isCountEnabled = false,
            isDeleteEnabled = false
        )
        assertEquals(expectedState, viewModel.state.value)
        assertFalse(cache.hasTransaction())
    }

    @Test
    fun `commit should call commit on cache and update state with error if no transactions`() {
        viewModel.commitTransaction()

        val expectedState = HomeState(
            key = "",
            value = "",
            commands = listOf(Command.Commit(success = false)),
            isGetEnabled = false,
            isSetEnabled = false,
            isCountEnabled = false,
            isDeleteEnabled = false
        )
        assertEquals(expectedState, viewModel.state.value)
        assertFalse(cache.hasTransaction())
    }

    @Test
    fun `it should handle multiple transactions`() {
        viewModel.beginTransaction()
        viewModel.changeKey("foo")
        viewModel.changeValue("456")
        viewModel.setKeyValue()
        viewModel.beginTransaction()
        viewModel.changeKey("foo")
        viewModel.delete()
        viewModel.rollbackTransaction()
        viewModel.commitTransaction()

        val commands = listOf(
            Command.Begin,
            Command.Set("foo", "456"),
            Command.Begin,
            Command.Delete("foo"),
            Command.Rollback(success = true),
            Command.Commit(success = true),
        )

        val expectedState = HomeState(
            key = "",
            value = "",
            commands = commands,
            isGetEnabled = false,
            isSetEnabled = false,
            isCountEnabled = false,
            isDeleteEnabled = false
        )
        assertEquals(expectedState, viewModel.state.value)
        assertFalse(cache.hasTransaction())
    }
}