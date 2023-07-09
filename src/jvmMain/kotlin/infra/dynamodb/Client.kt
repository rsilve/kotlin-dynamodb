package infra.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import aws.sdk.kotlin.services.dynamodb.paginators.queryPaginated
import aws.sdk.kotlin.services.dynamodb.paginators.scanPaginated
import aws.sdk.kotlin.services.dynamodb.waiters.waitUntilTableExists
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import model.TableItem
import model.tableItemDecoder
import model.tableItemEncoder

suspend fun dynClient() = DynamoDbClient.fromEnvironment {
    region = "eu-west-3"
}

fun close(client: DynamoDbClient) = client.close()

suspend fun verifyTable(client: DynamoDbClient, table: String): Boolean {
    return try {
        client.describeTable(DescribeTableRequest {
            tableName = table
        })
        true
    } catch (e: ResourceNotFoundException) {
        false
    }
}

suspend fun createTable(client: DynamoDbClient, table: String) {
    val attributesDef = listOf(
        AttributeDefinition {
            attributeName = "clientCode"
            attributeType = ScalarAttributeType.S
        },
        AttributeDefinition {
            attributeName = "countryCode"
            attributeType = ScalarAttributeType.S
        }
    )
    val keySchemaVal = listOf(KeySchemaElement {
        attributeName = "clientCode"
        keyType = KeyType.Hash
    }, KeySchemaElement {
        attributeName = "countryCode"
        keyType = KeyType.Range
    })

    val gsi = listOf(GlobalSecondaryIndex {
        indexName = "gsiCountryCode"
        keySchema = listOf(KeySchemaElement {
            attributeName = "countryCode"
            keyType = KeyType.Hash
        })
        projection = Projection {
            projectionType = ProjectionType.All
        }
    })


    client.createTable(CreateTableRequest {
        tableName = table
        attributeDefinitions = attributesDef
        keySchema = keySchemaVal
        globalSecondaryIndexes = gsi
        billingMode = BillingMode.PayPerRequest
    })
    client.waitUntilTableExists(DescribeTableRequest {
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

    val list = tableItems.map {
        val itemValues = tableItemEncoder(it)
        PutRequest {
            item = itemValues
        }
    }.map { WriteRequest { putRequest = it } }

    val batch = mutableMapOf<String, List<WriteRequest>>()
    batch[table] = list

    val request = BatchWriteItemRequest {
        requestItems = batch
    }

    client.batchWriteItem(request)
}


fun scanPaginated(
    filter: Map<String, String>,
    client: DynamoDbClient,
    table: String
): Flow<Pair<List<TableItem>?, ConsumedCapacity?>> {
    val request = if (filter.isEmpty()) {
        ScanRequest {
            tableName = table
            returnConsumedCapacity = ReturnConsumedCapacity.Total
        }
    } else {
        val filterExpr = filter.map { "begins_with (${it.key}, :${it.key})" }.joinToString(" AND ")
        val attributesValues = filter.mapKeys { ":${it.key}" }.mapValues { AttributeValue.S(it.value) }
        ScanRequest {
            tableName = table
            expressionAttributeValues = attributesValues
            filterExpression = filterExpr
            returnConsumedCapacity = ReturnConsumedCapacity.Total
        }
    }
    return client.scanPaginated(request)
        .map { value: ScanResponse -> Pair(value.items?.map { tableItemDecoder(it) }, value.consumedCapacity) }
}


fun queryPaginated(
    clientCode: String,
    countryCode: String?,
    filter: Map<String, String>,
    client: DynamoDbClient,
    table: String
): Flow<Pair<List<TableItem>?, ConsumedCapacity?>> {

    val values = mutableMapOf<String, AttributeValue>()
    values[":clientCode"] = AttributeValue.S(clientCode)
    var keyExpression = "clientCode = :clientCode"
    if (countryCode != null) {
        values[":countryCode"] = AttributeValue.S(countryCode)
        keyExpression += " AND begins_with (countryCode, :countryCode)"
    }
    val request = if (filter.isEmpty()) {
        QueryRequest {
            tableName = table
            keyConditionExpression = keyExpression
            expressionAttributeValues = values
            returnConsumedCapacity = ReturnConsumedCapacity.Total
        }
    } else {
        val filterExpr = filter.map { "begins_with (${it.key}, :${it.key})" }.joinToString(" AND ")
        filter.mapKeys { ":${it.key}" }.mapValues { AttributeValue.S(it.value) }.forEach { values[it.key] = it.value }

        QueryRequest {
            tableName = table
            keyConditionExpression = keyExpression
            filterExpression = filterExpr
            expressionAttributeValues = values
            returnConsumedCapacity = ReturnConsumedCapacity.Total
        }
    }

    return client.queryPaginated(request)
        .map { value: QueryResponse -> Pair(value.items?.map { tableItemDecoder(it) }, value.consumedCapacity) }
}
