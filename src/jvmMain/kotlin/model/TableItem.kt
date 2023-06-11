package model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.*


data class TableItem(val pk: String, val date: LocalDateTime)

fun tableItemGenerator() =  TableItem(
    UUID.randomUUID().toString(),
    Clock.System.now().toLocalDateTime(TimeZone.UTC)
)