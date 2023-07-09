package model

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue


fun addressEncoder(address: Address): Map<String, AttributeValue> {
    val itemValues = mutableMapOf<String, AttributeValue>()
    itemValues["streetAddress"] = AttributeValue.S(address.streetAddress)
    itemValues["city"] = AttributeValue.S(address.city)
    itemValues["postCode"] = AttributeValue.S(address.postCode)
    itemValues["countryCode"] = AttributeValue.S(address.countryCode)
    return itemValues
}

fun vehicleEncoder(vehicle: Vehicle): Map<String, AttributeValue> {
    val itemValues = mutableMapOf<String, AttributeValue>()
    itemValues["model"] = AttributeValue.S(vehicle.model)
    itemValues["fuelType"] = AttributeValue.S(vehicle.fuelType)
    itemValues["doors"] = AttributeValue.N(vehicle.doors.toString())
    itemValues["licensePlate"] = AttributeValue.S(vehicle.licensePlate)
    return itemValues
}

fun jobEncoder(job: Job): Map<String, AttributeValue> {
    val itemValues = mutableMapOf<String, AttributeValue>()
    itemValues["title"] = AttributeValue.S(job.title)
    itemValues["employmentType"] = AttributeValue.S(job.employmentType)
    itemValues["seniority"] = AttributeValue.S(job.seniority)
    itemValues["position"] = AttributeValue.S(job.position)
    itemValues["field"] = AttributeValue.S(job.field)
    return itemValues
}

fun bankEncoder(bank: Bank): Map<String, AttributeValue> {
    val itemValues = mutableMapOf<String, AttributeValue>()
    itemValues["name"] = AttributeValue.S(bank.name)
    itemValues["swiftBic"] = AttributeValue.S(bank.swiftBic)
    itemValues["iban"] = AttributeValue.S(bank.iban)
    return itemValues
}

fun deviceEncoder(device: Device): Map<String, AttributeValue> {
    val itemValues = mutableMapOf<String, AttributeValue>()
    itemValues["modelName"] = AttributeValue.S(device.modelName)
    itemValues["platform"] = AttributeValue.S(device.platform)
    itemValues["manufacturer"] = AttributeValue.S(device.manufacturer)
    itemValues["serial"] = AttributeValue.S(device.serial)
    return itemValues
}

fun peopleEncoder(people: People): Map<String, AttributeValue> {
    val itemValues = mutableMapOf<String, AttributeValue>()
    itemValues["id"] = AttributeValue.S(people.id)
    itemValues["name"] = AttributeValue.S(people.name)
    itemValues["address"] = AttributeValue.M(addressEncoder(people.address))
    itemValues["vehicle"] = AttributeValue.M(vehicleEncoder(people.vehicle))
    itemValues["quotes"] = AttributeValue.L(people.quotes.map { AttributeValue.S(it) })
    itemValues["job"] = AttributeValue.M(jobEncoder(people.job))
    itemValues["bank"] = AttributeValue.M(bankEncoder(people.bank))
    itemValues["devices"] = AttributeValue.L(people.devices.map { AttributeValue.M(deviceEncoder(it)) })

    return itemValues
}

fun tableItemEncoder(item: TableItem): Map<String, AttributeValue> {
    val itemValues = mutableMapOf<String, AttributeValue>()
    itemValues["clientCode"] = AttributeValue.S(item.clientCode)
    itemValues["countryCode"] = AttributeValue.S("${item.data.address.countryCode}#${item.data.id}")
    itemValues["fuelType"] = AttributeValue.S(item.data.vehicle.fuelType)
    itemValues["data"] = AttributeValue.M(peopleEncoder(item.data))
    return itemValues
}

fun tableItemDecoder(attr: Map<String, AttributeValue>): TableItem {
    return TableItem(
        attr["clientCode"]!!.asS(),
        peopleDecoder(attr["data"]!!.asM())
    )
}

fun jobDecoder(attr: Map<String, AttributeValue>): Job {
    return Job(
        attr["title"]!!.asS(),
        attr["employmentType"]!!.asS(),
        attr["seniority"]!!.asS(),
        attr["position"]!!.asS(),
        attr["field"]!!.asS()
    )
}

fun bankDecoder(attr: Map<String, AttributeValue>): Bank {
    return Bank(
        attr["name"]!!.asS(),
        attr["swiftBic"]!!.asS(),
        attr["iban"]!!.asS()
    )
}

fun devicesDecoder(attr: Map<String, AttributeValue>): Device {
    return Device(
        attr["modelName"]!!.asS(),
        attr["platform"]!!.asS(),
        attr["manufacturer"]!!.asS(),
        attr["serial"]!!.asS()
    )
}

fun peopleDecoder(attr: Map<String, AttributeValue>): People {
    return People(
        attr["id"]!!.asS(),
        attr["name"]!!.asS(),
        addressDecoder(attr["address"]!!.asM()),
        vehicleDecoder(attr["vehicle"]!!.asM()),
        attr["quotes"]!!.asL().map { it.asS() },
        jobDecoder(attr["job"]!!.asM()),
        bankDecoder(attr["bank"]!!.asM()),
        attr["devices"]!!.asL().map { devicesDecoder(it.asM()) }
    )
}


fun vehicleDecoder(attr: Map<String, AttributeValue>): Vehicle {
    return Vehicle(
        attr["model"]!!.asS(),
        attr["fuelType"]!!.asS(),
        attr["doors"]!!.asN().toInt(),
        attr["licensePlate"]!!.asS()
    )
}

fun addressDecoder(attr: Map<String, AttributeValue>): Address {
    return Address(
        attr["streetAddress"]!!.asS(),
        attr["city"]!!.asS(),
        attr["postCode"]!!.asS(),
        attr["countryCode"]!!.asS()
    )
}
