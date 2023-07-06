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
fun itemsList(list: List<TableItem>? = emptyList(), scanDuration: Long) {
    Column {
        Row {
            Text("Count: ${list?.size ?: 0}", modifier = Modifier.padding(end = 4.dp))
            Text("scan duration: ${scanDuration}ms")
        }
        LazyColumn {
            items(list ?: emptyList(), key = { it.pk }) { item ->
                val date = item.date.date.toString()
                ListItem(
                    icon = { badge(item.data.name) },
                    text = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.data.name, modifier = Modifier.padding(end = 8.dp))
                                Text(item.data.address.city, fontSize = MaterialTheme.typography.body2.fontSize)
                            }
                            Row(modifier = Modifier.padding(4.dp)) {
                                Text(date, color = MaterialTheme.colors.secondary, modifier = Modifier.padding(end = 4.dp))
                                Text(item.data.address.countryCode, color = MaterialTheme.colors.secondary)
                            }
                        }

                    }
                )
            }
        }
    }

}