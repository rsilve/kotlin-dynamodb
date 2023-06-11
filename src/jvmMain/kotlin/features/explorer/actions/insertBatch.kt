package features.explorer.actions

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import model.tableItemGenerator
import services.Client
import kotlin.system.measureTimeMillis

@Composable
fun insertBatch(size: Int, client: Client) {
    val coroutineScope = rememberCoroutineScope()
    var running by remember { mutableStateOf(false) }

    fun run(block: suspend () -> Unit) {
        coroutineScope.launch {
            running = true
            block()
            running = false
        }
    }

    Button(
        modifier = Modifier.width(180.dp),
        enabled = !running,
        onClick = {
            run {
                val timeMillis = measureTimeMillis {
                    val sequence = generateSequence { tableItemGenerator() }.take(size)
                    client.batchPut(sequence)
                }
                println(timeMillis)
            }
        }) {
        if (running) {
            CircularProgressIndicator(
                Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text("Add $size")
        }


    }

}