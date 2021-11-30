package com.shopify.library.internal.star

interface StarSdk {
    suspend fun searchPrinters(target: StarQuery): List<PortInfo>
    suspend fun print(port: Port, releasePort: Boolean): Boolean
    suspend fun getWifiPrinterStatus(portInfo: PortInfo, timesToReleasePort: Int): String

    enum class StarQuery(val query: String) {
        Bluetooth("BT:"),
        Network("TCP:"),
        Usb("USB:")
    }
}