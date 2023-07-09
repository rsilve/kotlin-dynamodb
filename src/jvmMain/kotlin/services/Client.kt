package services

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.ConsumedCapacity
import infra.dynamodb.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import model.TableItem

class Client(private val client: DynamoDbClient, private val table: String) {

    companion object {
        suspend fun createClient(table: String): Client {
            val client = dynClient()
            val exists = verifyTable(client, table)
            if (!exists) {
                createTable(client, table)
            }
            return Client(client, table)
        }
    }

    suspend fun put(item: TableItem) {
        putItemInTable(client, item, table)
    }

    suspend fun batchPut(items: Sequence<TableItem>) = coroutineScope {
        items.chunked(25).map {
            async {
                batchPutItemInTable(client, it, table)
            }
        }.chunked(3).forEach {
            it.awaitAll()
        }
    }

    fun scan(filter: Map<String, String> = emptyMap()): Flow<Pair<List<TableItem>?, ConsumedCapacity?>> =
        scanPaginated(filter, client, table)

    fun query(
        clientCode: String,
        countryCode: String?,
        filter: Map<String, String> = emptyMap()
    ): Flow<Pair<List<TableItem>?, ConsumedCapacity?>> = queryPaginated(clientCode, countryCode, filter, client, table)

    fun closeConnection() = close(client)
}