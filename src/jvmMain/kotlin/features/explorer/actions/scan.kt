package features.explorer.actions

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*

@Composable
fun scan(handleScan: (onComplete: () -> Unit) -> Unit) {
    var running by remember { mutableStateOf(false) }

    Box {
        Button(enabled = !running, onClick = {
            running = true
            handleScan { running = false}
        }) {
            Text("scan")
        }

    }
}