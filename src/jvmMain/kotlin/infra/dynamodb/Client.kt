package infra.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import kotlinx.coroutines.delay
import model.TableItem

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
    val itemValues = mutableMapOf<String, AttributeValue>()

    // Add all content to the table.
    itemValues["pk"] = AttributeValue.S(tableItem.pk)
    itemValues["date"] = AttributeValue.S(tableItem.date.toString())

    val request = PutItemRequest {
        tableName = table
        item = itemValues
    }

    dynClient().use { ddb ->
        ddb.putItem(request)
    }
}


suspend fun updateTableThroughput(ddb: DynamoDbClient, table: String, read: Long = 5, write: Long = 5) {
    val describeTable: DescribeTableResponse = ddb.describeTable(DescribeTableRequest {
        tableName = table
    })
    val provisioned = describeTable.table?.provisionedThroughput
    if (provisioned?.readCapacityUnits != read || provisioned.writeCapacityUnits != write) {
        ddb.updateTable(UpdateTableRequest {
            tableName = table
            provisionedThroughput = ProvisionedThroughput {
                writeCapacityUnits = write
                readCapacityUnits = read
            }
        })
        waitForActiveTable(ddb, table)
    }

}

suspend fun batchPutItemInTable(tableItems: List<TableItem>, table: String) {

    val list = tableItems
        .map {
            val itemValues = mutableMapOf<String, AttributeValue>()
            itemValues["pk"] = AttributeValue.S(it.pk)
            itemValues["date"] = AttributeValue.S(it.date.toString())
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

