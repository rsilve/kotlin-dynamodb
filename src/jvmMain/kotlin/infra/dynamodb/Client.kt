package infra.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import aws.sdk.kotlin.services.dynamodb.paginators.items
import aws.sdk.kotlin.services.dynamodb.paginators.scanPaginated
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import model.TableItem
import model.tableItemDecoder
import model.tableItemEncoder

suspend fun dynClient() = DynamoDbClient.fromEnvironment {
    region = "eu-west-3"
}

suspend fun tableInfo(ddb: DynamoDbClient, table: String): DescribeTableResponse {
    return ddb.describeTable(DescribeTableRequest {
        tableName = table
    })
}

suspend fun waitForActiveTable(ddb: DynamoDbClient, table: String) {
    var active = false
    while (!active) {
        val describeTable: DescribeTableResponse = tableInfo(ddb, table)
        active = describeTable.table?.tableStatus == TableStatus.Active
        if (!active) {
            println("Not active ... wait 1s")
            delay(1000)
        } else {
            println("Active")
        }
    }
}

suspend fun verifyTable(table: String) {
    return dynClient().use { ddb ->
        tableInfo(ddb, table)
    }
}


suspend fun putItemInTable(tableItem: TableItem, table: String) {
    val itemValues = tableItemEncoder(tableItem)

    val request = PutItemRequest {
        tableName = table
        item = itemValues
    }

    dynClient().use { ddb ->
        ddb.putItem(request)
    }
}


suspend fun batchPutItemInTable(tableItems: List<TableItem>, table: String) {

    val list = tableItems
        .map {
            val itemValues = tableItemEncoder(it)
            PutRequest {
                item = itemValues
            }
        }
        .map { WriteRequest { putRequest = it } }

    val batch = mutableMapOf<String, List<WriteRequest>>()
    batch[table] = list

    val request = BatchWriteItemRequest {
        requestItems = batch
    }

    dynClient().use { ddb ->
        ddb.batchWriteItem(request)
    }
}


suspend fun scanPaginated(table: String): List<TableItem> {
    return dynClient().use { ddb ->
        val flow = ddb.scanPaginated(ScanRequest {
            tableName = table
        })
        flow.items()
            .map { tableItemDecoder(it) }
            .toList()
    }
}
