package com.shopify.library.internal.star

interface StarSdk {
    suspend fun searchPrinters(target: StarQuery): List<PortInfo>

    enum class StarQuery(val query: String) {
        Bluetooth("BT:"),
        Network("TCP:"),
        Usb("USB:")
    }
}