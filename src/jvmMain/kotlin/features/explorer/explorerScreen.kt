package features.explorer

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import features.explorer.actions.insertBatch
import features.explorer.actions.insertOne
import services.Client

@Composable
fun explorerScreen(client: Client) {

        Column(modifier = Modifier.padding(4.dp)) {
            Row { Text("Hello") }
            Row {
                insertOne(client)
                Box(modifier = Modifier.width(4.dp)) {  }
                insertBatch(10000, client)
            }
        }

}