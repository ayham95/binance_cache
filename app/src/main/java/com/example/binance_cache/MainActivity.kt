package com.example.binance_cache

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.binance_cache.ui.theme.BinanceCacheTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BinanceCacheTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),

        ) {
        CommandList()
        ControlPanel()
    }
}

@Composable
fun CommandList() {
    val viewModel = viewModel<HomeViewModel>()
    val items = viewModel.state.collectAsStateWithLifecycle().value.commands
    Column {
        Text(text = "List")
        LazyColumn {
            items(items) { item ->
                Text(text = item)
            }
        }
    }
}

@Composable
fun ControlPanel() {
    val viewModel = viewModel<HomeViewModel>()

    Column {
        KeyValueFields()
        CrudButtons(
            onGetClicked = { viewModel.fetch() },
            onSetClicked = { viewModel.setKeyValue() },
            onDeleteClicked = { viewModel.delete() },
            onCountClicked = { viewModel.count() },
        )
        TransactionButtons(
            onBeginClicked = { viewModel.beginTransaction() },
            onCommitClicked = { viewModel.commitTransaction() },
            onRollbackClicked = { viewModel.rollbackTransaction() },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyValueFields() {
    val viewModel = viewModel<HomeViewModel>()
    val keyText = viewModel.state.collectAsStateWithLifecycle().value.key
    val valueText = viewModel.state.collectAsStateWithLifecycle().value.value

    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        TextField(
            value = keyText,
            onValueChange = { viewModel.changeKey(it) },
            label = { Text(text = "Enter Key") },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
            value = valueText,
            onValueChange = { viewModel.changeValue(it) },
            label = { Text(text = "Enter Value") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CrudButtons(
    onGetClicked: () -> Unit,
    onSetClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onCountClicked: () -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
    ) {
        item {
            Button(onClick = onGetClicked) {
                Text("GET")
            }
        }
        item {
            Button(onClick = onSetClicked) {
                Text("SET")
            }
        }
        item {
            Button(onClick = onDeleteClicked) {
                Text("DELETE")
            }
        }
        item {
            Button(onClick = onCountClicked) {
                Text("COUNT")
            }
        }
    }
}

@Composable
fun TransactionButtons(
    onBeginClicked: () -> Unit,
    onRollbackClicked: () -> Unit,
    onCommitClicked: () -> Unit,
) {
    Column {
        Text(text = "Transaction")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onBeginClicked) {
                Text("BEGIN")
            }
            Button(onClick = onRollbackClicked) {
                Text("ROLLBACK")
            }
            Button(onClick = onCommitClicked) {
                Text("COMMIT")
            }
        }
    }
}