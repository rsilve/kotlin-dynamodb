package features.explorer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import aws.sdk.kotlin.services.dynamodb.model.ConsumedCapacity
import features.explorer.search.query
import features.explorer.search.scan
import kotlinx.coroutines.flow.flowOf
import model.TableItem
import services.Client
import kotlin.system.measureTimeMillis


val clientCodeExample = "Ganjaflex"

@Composable
fun examplePanel(client: Client) {
    val (flow, setFlow) = remember {
        mutableStateOf(
            flowOf(
                Pair(
                    null,
                    null
                ) as Pair<List<TableItem>?, ConsumedCapacity?>
            )
        )
    }
    var items by remember { mutableStateOf(emptyList<TableItem>()) }
    val (duration, setDuration) = remember { mutableStateOf(-1L) }
    var readCapacity by remember { mutableStateOf(-1.0) }
    var running by remember { mutableStateOf(false) }

    LaunchedEffect(flow) {
        setDuration(-1L)
        readCapacity = -1.0
        var read = 0.0
        running = true
        items = emptyList()
        val timeMillis = measureTimeMillis {
            flow.collect {
                val list = mutableListOf<TableItem>()
                list.addAll(it.first ?: emptyList())
                list.addAll(items)
                items = list
                println(it.second)
                read += it.second?.capacityUnits ?: 0.0
            }
        }
        setDuration(timeMillis)
        readCapacity = read
        running = false
    }

    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            scan("scan ALL", emptyMap(), client, setFlow)
            scan("scan FR", mapOf(Pair("countryCode", "FR#")), client, setFlow)
            scan("scan Gasoline", mapOf(Pair("fuelType", "Gasoline")), client, setFlow)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            scan("scan $clientCodeExample", mapOf(Pair("clientCode", clientCodeExample)), client, setFlow)
            scan(
                "scan $clientCodeExample FR",
                mapOf(Pair("clientCode", clientCodeExample), Pair("countryCode", "FR#")),
                client,
                setFlow
            )

        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            query("query $clientCodeExample", clientCodeExample, client = client, setFlow = setFlow)
            query("query $clientCodeExample FR", clientCodeExample, countryCode = "FR#", client = client, setFlow = setFlow)
            query("query $clientCodeExample Gasoline", clientCodeExample, filter = mapOf(Pair("fuelType", "Gasoline")), client = client, setFlow = setFlow)
        }
        itemsList(items, duration, readCapacity)
    }
}