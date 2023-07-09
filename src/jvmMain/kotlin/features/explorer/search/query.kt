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
fun query(
    text: String,
    clientCode: String,
    countryCode: String? = null,
    filter: Map<String, String> = emptyMap(),
    client: Client,
    setFlow: (Flow<Pair<List<TableItem>?, ConsumedCapacity?>>) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()

    val handleQuery: (() -> Unit) -> Unit = { complete ->
        coroutineScope.launch {
            val flow = client.query(clientCode, countryCode, filter)
            setFlow(flow)
        }
        complete()
    }

    buttonSearch(text, handleQuery)
}