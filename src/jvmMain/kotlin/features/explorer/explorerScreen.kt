package features.explorer

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import features.explorer.actions.insertBatch
import features.explorer.actions.scan
import kotlinx.coroutines.launch
import model.TableItem
import model.tableItemGenerator
import services.Client
import kotlin.system.measureTimeMillis

const val INSERT_BATCH_SIZE = 10000


@Composable
fun explorerScreen(client: Client) {
    val coroutineScope = rememberCoroutineScope()
    val flow = remember { client.scan() }
    var items by remember { mutableStateOf(emptyList<TableItem>()) }

    val handleInsertOne: (() -> Unit) -> Unit = { complete ->
        coroutineScope.launch {
            val timeMillis = measureTimeMillis {
                client.put(tableItemGenerator())
            }
            println(timeMillis)
            complete()
        }
    }

    val handleInsertBatch: (() -> Unit) -> Unit = { complete ->
        coroutineScope.launch {
            val timeMillis = measureTimeMillis {
                val sequence = generateSequence { tableItemGenerator() }.take(INSERT_BATCH_SIZE)
                client.batchPut(sequence)
            }
            println(timeMillis)
            complete()
        }
    }

    val handleScan: (() -> Unit) -> Unit = { complete ->
        coroutineScope.launch {
            val timeMillis = measureTimeMillis {
                items = emptyList()
                flow.collect {
                    val list = mutableListOf<TableItem>()
                    list.addAll(items)
                    list.addAll(it?: emptyList())
                    items = list
                }
            }
            println(timeMillis)
            complete()
        }
    }

    Column(modifier = Modifier.padding(4.dp)) {
        Row { Text("Hello") }
        Row {
            insertBatch(1, handleInsertOne)
            Box(modifier = Modifier.width(4.dp)) { }
            insertBatch(INSERT_BATCH_SIZE, handleInsertBatch)
            Box(modifier = Modifier.width(4.dp)) { }
            scan(handleScan)
        }
        Row {
            itemsList(items)
        }
    }

}