package infra.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import aws.sdk.kotlin.services.dynamodb.paginators.queryPaginated
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
            attributeName = "pk"
            attributeType = ScalarAttributeType.S
        },
        AttributeDefinition {
            attributeName = "date"
            attributeType = ScalarAttributeType.S
        },
        AttributeDefinition {
            attributeName = "countryCode"
            attributeType = ScalarAttributeType.S
        },
    )
    val keySchemaVal = listOf(KeySchemaElement {
        attributeName = "pk"
        keyType = KeyType.Hash
    }, KeySchemaElement {
        attributeName = "date"
        keyType = KeyType.Range
    })

    val gsi = listOf(GlobalSecondaryIndex {
        indexName = "country"
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


fun scanPaginated(client: DynamoDbClient, table: String): Flow<List<TableItem>?> {
    val values = mutableMapOf<String, AttributeValue>()
    values[":cc"] = AttributeValue.S("FR")

    return client.scanPaginated(ScanRequest {
        tableName = table
        expressionAttributeValues = values
        filterExpression = "countryCode = :cc"

    }).map { value: ScanResponse -> value.items?.map { tableItemDecoder(it) } }
}


fun queryPaginated(client: DynamoDbClient, table: String): Flow<List<TableItem>?> {
    val values = mutableMapOf<String, AttributeValue>()
    values[":d"] = AttributeValue.S("2024")
    values[":cc"] = AttributeValue.S("FR")
    values[":zero"] = AttributeValue.S("0")
    val names = mutableMapOf<String, String>()
    names["#date"] = "date"

    return client.queryPaginated(QueryRequest {
        tableName = table
        keyConditionExpression = "#date > :d"
        filterExpression = "countryCode = :cc"
        expressionAttributeValues = values
        expressionAttributeNames = names
    }).map { value: QueryResponse -> value.items?.map { tableItemDecoder(it) } }
}
