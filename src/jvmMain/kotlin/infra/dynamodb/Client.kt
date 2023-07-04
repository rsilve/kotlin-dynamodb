package infra.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import aws.sdk.kotlin.services.dynamodb.paginators.scanPaginated
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import model.TableItem
import model.tableItemDecoder
import model.tableItemEncoder

suspend fun dynClient() = DynamoDbClient.fromEnvironment {
    region = "eu-west-3"
}

fun close(client: DynamoDbClient) = client.close()

suspend fun verifyTable(client: DynamoDbClient, table: String): DescribeTableResponse {
    return client.describeTable(DescribeTableRequest {
        tableName = table
    })
}


suspend fun putItemInTable(client: DynamoDbClient, tableItem: TableItem, table: String) {
    val itemValues = tableItemEncoder(tableItem)

    val request = PutItemRequest {
        tableName = table
        item = itemValues
    }

    client.putItem(request)
}


suspend fun batchPutItemInTable(client: DynamoDbClient, tableItems: List<TableItem>, table: String) {

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

    client.batchWriteItem(request)
}


fun scanPaginated(client: DynamoDbClient, table: String): Flow<List<TableItem>?> {
    return client.scanPaginated(ScanRequest {
        tableName = table
    }).map { value: ScanResponse -> value.items?.map { tableItemDecoder(it) } }
}
