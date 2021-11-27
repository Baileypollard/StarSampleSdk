package com.shopify.library.internal.star

expect class Port

interface PortInfo {
    val portName: String
    val macAddress: String
    val modelName: String
}