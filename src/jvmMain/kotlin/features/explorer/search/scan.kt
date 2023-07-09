package features.explorer.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import aws.sdk.kotlin.services.dynamodb.model.ConsumedCapacity
import features.explorer.actions.buttonSearch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import model.TableItem
import services.Client

@Composable
fun scan(
    text: String,
    filter: Map<String, String> = emptyMap(),
    client: Client,
    setFlow: (Flow<Pair<List<TableItem>?, ConsumedCapacity?>>) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()

    val handleScan: (() -> Unit) -> Unit = { complete ->
        coroutineScope.launch {
            val flow = client.scan(filter)
            setFlow(flow)
        }
        complete()
    }


    buttonSearch(text, handleScan)
}