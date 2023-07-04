package features.explorer.actions

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun insertBatch(size: Int, handleInsert: (onComplete: () -> Unit) -> Unit) {
    var running by remember { mutableStateOf(false) }

    Button(
        modifier = Modifier.width(180.dp),
        enabled = !running,
        onClick = {
            running = true
            handleInsert {running = false}
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