package features.explorer

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import features.explorer.actions.insertBatch
import kotlinx.coroutines.launch
import model.tableItemGenerator
import services.Client
import kotlin.system.measureTimeMillis

const val INSERT_BATCH_SIZE = 100000


@Composable
fun explorerScreen(client: Client) {
    val coroutineScope = rememberCoroutineScope()

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


    Column(modifier = Modifier.padding(4.dp)) {
        Row {
            insertBatch(1, handleInsertOne)
            Box(modifier = Modifier.width(4.dp)) { }
            insertBatch(INSERT_BATCH_SIZE, handleInsertBatch)
            Box(modifier = Modifier.width(4.dp)) { }
        }
        examplePanel(client)

    }

}