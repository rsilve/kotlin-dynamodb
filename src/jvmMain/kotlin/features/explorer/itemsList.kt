package features.explorer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.TableItem

@Composable
fun badge(name: String) {
    Text(
        name.substring(0, 2),
        color = MaterialTheme.colors.background,
        fontSize = MaterialTheme.typography.h2.fontSize,
        modifier = Modifier.width(48.dp).height(48.dp)
            .background(color = MaterialTheme.colors.onBackground, shape = CircleShape)

    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun itemsList(list: List<TableItem> = emptyList()) {
    LazyColumn {
        items(list, key = { it.pk }) { item ->
            ListItem(
                icon = { badge(item.data.name) },
                text = {
                    Row {
                        Text(item.data.name)
                        Text(item.data.address.city)
                    }
                }
            )
        }
    }
}