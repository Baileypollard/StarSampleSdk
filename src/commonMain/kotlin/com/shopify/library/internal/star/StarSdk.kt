package com.shopify.library.internal.star

/**
 * Interface responsible for wrapping the platform specific implementation of the StarSDK
 * Normally, in a production app we would have both iOS and Android implementations of this
 * interface - but for the purposes of this sample project we only implement iOS.
 */
interface StarSdk {
    suspend fun searchPrinters(target: StarQuery): List<PortInfo>
    suspend fun print(port: Port, releasePort: Boolean): Boolean
    suspend fun getWifiPrinterStatus(portInfo: PortInfo, timesToReleasePort: Int): String

    enum class StarQuery(val query: String) {
        Bluetooth("BT:"),
        Network("TCP:"),
        Usb("USB:"),
        All("All:")
    }
}