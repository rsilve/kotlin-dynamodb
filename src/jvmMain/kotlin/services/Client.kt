package services

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
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
        }.chunked(100).forEach {
            it.awaitAll()
        }
    }

    fun scan(): Flow<List<TableItem>?> = scanPaginated(client, table)
    fun query(): Flow<List<TableItem>?> = queryPaginated(client, table)

    fun closeConnection() = close(client)
}