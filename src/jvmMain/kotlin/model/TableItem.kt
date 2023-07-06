package model

import io.github.serpro69.kfaker.Faker
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.util.*
import kotlin.random.Random

val faker = Faker()


data class Address(val streetAddress: String, val city: String, val postCode: String, val countryCode: String)
data class Vehicle(val model: String, val fuelType: String, val doors: Int, val licensePlate: String)
data class Customer(val name: String, val address: Address, val vehicle: Vehicle, val quotes: List<String>)

data class TableItem(val pk: String, val date: LocalDateTime, val data: Customer)

fun tableItemGenerator() = TableItem(
    UUID.randomUUID().toString(),
    Clock.System.now().plus(Random.nextInt(-20, 20), DateTimeUnit.YEAR, TimeZone.UTC).toLocalDateTime(TimeZone.UTC),
    Customer(
        faker.name.name(),
        Address(
            faker.address.streetAddress(),
            faker.address.city(),
            faker.address.postcode(),
            faker.address.countryCode()
        ),
        Vehicle(
            "${faker.vehicle.makes()}-${faker.vehicle.modelsByMake("")}",
            faker.vehicle.fuelTypes(),
            faker.vehicle.doors().toInt(),
            faker.vehicle.licensePlate()
        ),
        generateSequence { faker.theOffice.quotes()}.take(5).toList()
    )
)