package features.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import features.connection.connectionScreen
import features.explorer.explorerScreen
import services.Client
import java.util.*

@Composable
@Preview
fun mainScreen() {

        val (client, setClient) = remember { mutableStateOf(Optional.empty<Client>()) }

        if (client.isEmpty) {
                connectionScreen(setClient)
        } else {
                explorerScreen(client.get())
        }
}