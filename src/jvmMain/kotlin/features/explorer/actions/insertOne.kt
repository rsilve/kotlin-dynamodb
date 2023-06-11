package features.explorer.actions

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import model.tableItemGenerator
import services.Client

@Composable
fun insertOne(client: Client) {
    val coroutineScope = rememberCoroutineScope()
    var running by remember { mutableStateOf(false) }

    fun run(block: suspend () -> Unit) {
        coroutineScope.launch {
            running = true
            block()
            running = false
        }
    }

    Box {
        Button(enabled = !running, onClick = {
            run { client.put(tableItemGenerator()) }
        }) {
            Text("Add one")
        }

    }
}