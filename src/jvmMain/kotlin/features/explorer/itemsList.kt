package features.explorer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import model.TableItem

@Composable
fun badge(name: String) {
    Text(
        name.substring(0, 2),
        color = MaterialTheme.colors.background,
        fontSize = MaterialTheme.typography.button.fontSize,
        modifier = Modifier.width(48.dp).height(48.dp)
            .background(color = MaterialTheme.colors.onBackground, shape = CircleShape)
            .wrapContentHeight(),
        textAlign = TextAlign.Center,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun itemsList(list: List<TableItem>? = emptyList(), scanDuration: Long, readCapacity: Double) {
    val duration = if (scanDuration < 0) "--" else "${scanDuration}ms"
    val consumed = if (readCapacity < 0) "--" else "$readCapacity"
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Count: ${list?.size ?: 0}")
            Text("scan duration: $duration")
            Text("read capacity consumed: $consumed")
        }
        LazyColumn {
            items(list ?: emptyList(), key = { it.data.id }) { item ->
                ListItem(
                    icon = { badge(item.data.name) },
                    text = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(item.data.name)
                                Text(item.clientCode)
                            }
                            Row(modifier = Modifier.padding(4.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(item.data.vehicle.fuelType, color = MaterialTheme.colors.secondary)
                                Text(item.data.address.countryCode, color = MaterialTheme.colors.secondary)
                            }
                        }

                    }
                )
            }
        }
    }

}