package features.connection

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import services.Client
import services.Client.Companion.createClient
import java.util.*
import kotlin.system.exitProcess


@Composable
@Preview
fun connectionScreen(setClient: (Optional<Client>) -> Unit) {

    var failure by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            setClient(Optional.of(createClient("test")))
        } catch (e: Exception) {
            e.printStackTrace()
            failure = true
        }

    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            if (failure) {
                Text("Connection failed")
                Button(onClick = { exitProcess(1) }) {
                    Text("Quit")
                }
            } else {
                CircularProgressIndicator()
                Text("Connecting ...")
            }
        }
    }

}