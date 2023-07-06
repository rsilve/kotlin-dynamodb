package model

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import kotlinx.datetime.LocalDateTime


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

fun customerEncoder(customer: Customer): Map<String, AttributeValue> {
    val itemValues = mutableMapOf<String, AttributeValue>()
    itemValues["name"] = AttributeValue.S(customer.name)
    itemValues["address"] = AttributeValue.M(addressEncoder(customer.address))
    itemValues["vehicle"] = AttributeValue.M(vehicleEncoder(customer.vehicle))
    itemValues["quotes"] = AttributeValue.L(customer.quotes.map { AttributeValue.S(it) })
    return itemValues
}


fun tableItemEncoder(item: TableItem): Map<String, AttributeValue> {
    val itemValues = mutableMapOf<String, AttributeValue>()
    itemValues["pk"] = AttributeValue.S(item.pk)
    itemValues["date"] = AttributeValue.S(item.date.toString())
    itemValues["data"] = AttributeValue.M(customerEncoder(item.data))
    itemValues["countryCode"] = AttributeValue.S(item.data.address.countryCode)
    return itemValues
}


fun tableItemDecoder(attr: Map<String, AttributeValue>): TableItem {
    return TableItem(attr["pk"]!!.asS(), LocalDateTime.parse(attr["date"]!!.asS()), consumerDecoder(attr["data"]!!.asM()))
}

fun consumerDecoder(attr: Map<String, AttributeValue>): Customer {
    return Customer(attr["name"]!!.asS(), addressDecoder(attr["address"]!!.asM()), vehicleDecoder(attr["vehicle"]!!.asM()), attr["quotes"]!!.asL().map { it.asS() })
}

fun vehicleDecoder(attr: Map<String, AttributeValue>): Vehicle {
    return Vehicle(attr["model"]!!.asS(), attr["fuelType"]!!.asS(), attr["doors"]!!.asN().toInt(), attr["licensePlate"]!!.asS())
}

fun addressDecoder(attr: Map<String, AttributeValue>): Address {
    return Address(attr["streetAddress"]!!.asS(), attr["city"]!!.asS(), attr["postCode"]!!.asS(), attr["countryCode"]!!.asS())
}
