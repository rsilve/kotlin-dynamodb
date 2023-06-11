package services

import infra.dynamodb.batchPutItemInTable
import infra.dynamodb.putItemInTable
import infra.dynamodb.verifyTable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import model.TableItem

class Client(private val table: String) {

    companion object {
        suspend fun createClient(table: String): Client {
            verifyTable(table)
            return Client(table)
        }
    }

    suspend fun put(item: TableItem) {
        putItemInTable(item, table)
    }

    suspend fun batchPut(items: Sequence<TableItem>) = coroutineScope {
        items.chunked(25).map {
            async {
                batchPutItemInTable(it, table)
            }
        }.chunked(50).forEach {
            it.awaitAll()
        }
    }
}