package model

import io.github.serpro69.kfaker.Faker
import java.util.*
import kotlin.random.Random

val faker = Faker()

data class Job(
    val title: String,
    val employmentType: String,
    val seniority: String,
    val position: String,
    val field: String
)

data class Address(val streetAddress: String, val city: String, val postCode: String, val countryCode: String)
data class Vehicle(val model: String, val fuelType: String, val doors: Int, val licensePlate: String)

data class Bank(val name: String, val swiftBic: String, val iban: String)

data class Device(val modelName: String, val platform: String, val manufacturer: String, val serial: String)

data class People(
    val id: String,
    val name: String,
    val address: Address,
    val vehicle: Vehicle,
    val quotes: List<String>,
    val job: Job,
    val bank: Bank,
    val devices: List<Device>
)

data class TableItem(val clientCode: String, val data: People)

val clientCodeList = arrayListOf(
    "Kemmer, Russel and Deckow",
    "Schoen-Nolan",
    "Effertz-Sipes",
    "Nicolas-Collier",
    "Ritchie, Olson and Hauck",
    "Lang, Quigley and Sporer",
    "Waters-Watsica",
    "Jerde and Sons",
    "Muller-Mann",
    "Will-Satterfield",
    "Openlane",
    "Yearin",
    "Goodsilron",
    "Condax",
    "Opentech",
    "Golddex",
    "year-job",
    "Isdom",
    "Gogozoom",
    "Y-corporation",
    "Nam-zim",
    "Donquadtech",
    "Warephase",
    "Donware",
    "Faxquote",
    "Sunnamplex",
    "Lexiqvolax",
    "Sumace",
    "Treequote",
    "Iselectrics",
    "Zencorporation",
    "Plusstrip",
    "dambase",
    "Toughzap",
    "Codehow",
    "Zotware",
    "Statholdings",
    "Conecom",
    "Zathunicon",
    "Labdrill",
    "Ron-tech",
    "Green-Plus",
    "Groovestreet",
    "Zoomit",
    "Bioplex",
    "Zumgoity",
    "Scotfind",
    "Dalttechnology",
    "Kinnamplus",
    "Konex",
    "Stanredtax",
    "Cancity",
    "Finhigh",
    "Kan-code",
    "Blackzim",
    "Dontechi",
    "Xx-zobam",
    "Fasehatice",
    "Hatfan",
    "Streethex",
    "Inity",
    "Konmatfix",
    "Bioholding",
    "Hottechi",
    "Ganjaflex",
    "Betatech",
    "Domzoom",
    "Ontomedia",
    "Newex",
    "Betasoloin",
    "Mathtouch",
    "Rantouch",
    "Silis",
    "Plussunin",
    "Plexzap",
    "Finjob",
    "Xx-holding",
    "Scottech",
    "Funholding",
    "Sonron",
    "Singletechno",
    "Rangreen",
    "J-Texon",
    "Rundofase",
    "Doncon",
)
val countryCodeList = arrayListOf("FR", "US", "DE", "IT", "UK")
fun tableItemGenerator() = TableItem(
    clientCodeList[faker.random.nextInt(clientCodeList.size)],
    People(
        UUID.randomUUID().toString(),
        faker.name.name(),
        Address(
            faker.address.streetAddress(),
            faker.address.city(),
            faker.address.postcode(),
            countryCodeList[faker.random.nextInt(countryCodeList.size)]
        ),
        Vehicle(
            "${faker.vehicle.makes()}-${faker.vehicle.modelsByMake("")}",
            faker.vehicle.fuelTypes(),
            faker.vehicle.doors().toInt(),
            faker.vehicle.licensePlate()
        ),
        generateSequence { faker.theOffice.quotes() }.take(5).toList(),
        Job(
            faker.job.title(),
            faker.job.employmentType(),
            faker.job.seniority(),
            faker.job.position(),
            faker.job.field()
        ),
        Bank(
            faker.bank.name(), faker.bank.swiftBic(), faker.bank.ibanDetails("")
        ),
        generateSequence {
            Device(
                faker.device.modelName(),
                faker.device.platform(),
                faker.device.manufacturer(),
                faker.device.serial()
            )
        }.take(Random.nextInt(1, 20)).toList()
    )
)
