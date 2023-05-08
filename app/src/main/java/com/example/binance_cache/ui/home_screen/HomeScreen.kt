package com.example.binance_cache.ui.home_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.binance_cache.R
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.binance_cache.di.HomeContainer


@Composable
fun HomeScreen() {

    val viewModel = HomeContainer.homeViewModelFactory.create(HomeViewModel::class.java)
    val state = viewModel.state.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        CommandList(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            commands = state.commands
        )
        KeyValueFields(
            onKeyChanged = { viewModel.changeKey(it) },
            onValueChanged = { viewModel.changeValue(it) },
            keyText = state.key,
            valueText = state.value,
        )
        CrudButtons(
            onGetClicked = { viewModel.fetch() },
            onSetClicked = { viewModel.setKeyValue() },
            onDeleteClicked = { viewModel.delete() },
            onCountClicked = { viewModel.count() },
            isGetEnabled = state.isGetEnabled,
            isSetEnabled = state.isSetEnabled,
            isDeleteEnabled = state.isDeleteEnabled,
            isCountEnabled = state.isCountEnabled
        )
        TransactionButtons(
            onBeginClicked = { viewModel.beginTransaction() },
            onCommitClicked = { viewModel.commitTransaction() },
            onRollbackClicked = { viewModel.rollbackTransaction() },
        )
    }
}

@Composable
fun CommandList(
    modifier: Modifier = Modifier, commands: List<Command>
) {
    val listState = rememberLazyListState()
    LaunchedEffect(commands.size) {
        listState.animateScrollToItem(commands.size)
    }
    Column(modifier = modifier) {
        Text(text = "Commands", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        LazyColumn(state = listState) {
            items(commands) { item ->
                when (item) {
                    is Command.Get -> GetCommandItem(command = item)
                    is Command.Set -> SetCommandItem(command = item)
                    is Command.Delete -> DeleteCommandItem(command = item)
                    is Command.Count -> CountCommandItem(command = item)
                    is Command.Commit -> CommitCommandItem(command = item)
                    is Command.Rollback -> RollbackCommandItem(command = item)
                    Command.Begin -> BeginCommandItem()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyValueFields(
    modifier: Modifier = Modifier,
    onKeyChanged: (String) -> Unit,
    onValueChanged: (String) -> Unit,
    keyText: String,
    valueText: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        TextField(
            value = keyText,
            onValueChange = onKeyChanged,
            label = { Text(text = stringResource(R.string.enter_key_hint)) },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
            value = valueText,
            onValueChange = onValueChanged,
            label = { Text(text = stringResource(R.string.enter_value_hint)) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CrudButtons(
    modifier: Modifier = Modifier,
    onGetClicked: () -> Unit,
    onSetClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onCountClicked: () -> Unit,
    isGetEnabled: Boolean = false,
    isSetEnabled: Boolean = false,
    isDeleteEnabled: Boolean = false,
    isCountEnabled: Boolean = false,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
    ) {
        item {
            Button(onClick = onGetClicked, enabled = isGetEnabled) {
                Text(stringResource(R.string.btn_get))
            }
        }
        item {
            Button(onClick = onSetClicked, enabled = isSetEnabled) {
                Text(stringResource(R.string.btn_set))
            }
        }
        item {
            Button(onClick = { showDeleteDialog = true }, enabled = isDeleteEnabled) {
                Text(stringResource(R.string.btn_delete))
                if (showDeleteDialog)
                    HomeAlertDialog(
                        title = stringResource(R.string.alert_dialog_delete_title),
                        message = stringResource(R.string.alert_dialog_body_text),
                        onDismiss = { showDeleteDialog = false },
                        onConfirm = {
                            onDeleteClicked()
                            showDeleteDialog = false
                        },
                    )
            }
        }
        item {
            Button(onClick = onCountClicked, enabled = isCountEnabled) {
                Text(stringResource(R.string.btn_count))
            }
        }
    }
}

@Composable
fun TransactionButtons(
    modifier: Modifier = Modifier,
    onBeginClicked: () -> Unit,
    onRollbackClicked: () -> Unit,
    onCommitClicked: () -> Unit,
) {
    var showCommitDialog by remember { mutableStateOf(false) }
    var showRollbackDialog by remember { mutableStateOf(false) }

    Column {
        Text(text = "Transactions", fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBeginClicked) {
                Text(stringResource(R.string.btn_begin))
            }
            Button(onClick = { showRollbackDialog = true }) {
                Text(stringResource(R.string.btn_rollback))
                if (showRollbackDialog)
                    HomeAlertDialog(
                        title = stringResource(R.string.alert_dialog_rollback_title),
                        message = stringResource(R.string.alert_dialog_body_text),
                        onDismiss = { showRollbackDialog = false },
                        onConfirm = {
                            onRollbackClicked()
                            showRollbackDialog = false
                        },
                    )

            }
            Button(onClick = { showCommitDialog = true }) {
                Text(stringResource(R.string.btn_commit))
                if (showCommitDialog)
                    HomeAlertDialog(
                        title = stringResource(R.string.alert_dialog_commit_title),
                        message = stringResource(R.string.alert_dialog_body_text),
                        onDismiss = { showCommitDialog = false },
                        onConfirm = {
                            onCommitClicked()
                            showCommitDialog = false
                        },
                    )
            }
        }
    }
}

@Composable
fun GetCommandItem(modifier: Modifier = Modifier, command: Command.Get) {
    val output = command.output ?: stringResource(R.string.value_not_set_error)
    Column(modifier = modifier) {
        Text(text = "> GET ${command.key}")
        Text(text = output, color = Color.Blue)
    }
}

@Composable
fun SetCommandItem(modifier: Modifier = Modifier, command: Command.Set) {
    Column(modifier = modifier) {
        Text(text = "> SET ${command.key} ${command.value}")
    }
}

@Composable
fun DeleteCommandItem(modifier: Modifier = Modifier, command: Command.Delete) {
    Column(modifier = modifier) {
        Text(text = "> DELETE ${command.key}")
    }
}

@Composable
fun CountCommandItem(modifier: Modifier = Modifier, command: Command.Count) {
    Column(modifier = modifier) {
        Text(text = "> COUNT ${command.value}")
        Text(text = "${command.output}", color = Color.Blue)
    }
}

@Composable
fun BeginCommandItem(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = stringResource(R.string.cmd_item_begin))
    }
}

@Composable
fun CommitCommandItem(modifier: Modifier = Modifier, command: Command.Commit) {
    Column(modifier = modifier) {
        Text(text = stringResource(R.string.cmd_item_commit))
        if (!command.success)
            Text(text = stringResource(R.string.no_transactions_error), color = Color.Blue)
    }
}

@Composable
fun RollbackCommandItem(modifier: Modifier = Modifier, command: Command.Rollback) {
    Column(modifier = modifier) {
        Text(text = stringResource(R.string.cmd_item_rollback))
        if (!command.success)
            Text(text = stringResource(R.string.no_transactions_error), color = Color.Blue)
    }
}

@Composable
fun HomeAlertDialog(
    title: String,
    message: String = "",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
            }) {
                Text(text = stringResource(R.string.alert_dialog_confirm_btn))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = stringResource(R.string.alert_dialog_dismiss_btn))
            }
        },
    )
}