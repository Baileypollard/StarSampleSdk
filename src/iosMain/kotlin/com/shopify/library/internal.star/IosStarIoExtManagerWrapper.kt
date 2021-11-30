package com.shopify.library.internal.star

import StarIO.SMPort
import StarIO_Extension.StarIoExtManager
import StarIO_Extension.StarIoExtManagerDelegateProtocol
import StarIO_Extension.StarIoExtManagerTypeStandard
import StarIO_Extension.connectAsync
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.native.concurrent.freeze

/**
 * iOS wrapper around the [StarIoExtManager] provided by the StarSDK.
 * This class allows us to connect, print and observe connection status updates for a
 * given Star Micronics printer.
 */
class IosStarIoExtManagerWrapper(private val starSdk: StarSdk) : StarIoExtManagerWrapper {
    private var starIoExtManager = atomic<StarIoExtManager?>(null)

    private val _printerStatus = MutableStateFlow("Uninitialized")
    override val printerStatus = _printerStatus.asStateFlow()

    override fun connect(portName: String) {
        starIoExtManager.value = StarIoExtManager(StarIoExtManagerTypeStandard, portName, "", 10_000).apply {
            delegate = object : NSObject(), StarIoExtManagerDelegateProtocol {
                override fun manager(manager: StarIoExtManager, didConnectPort: String) {
                    _printerStatus.value = "Initial connection succeeded"
                }

                override fun manager(manager: StarIoExtManager, didFailToConnectPort: String, error: NSError?) {
                    _printerStatus.value = "Initial connection failed"
                }

                override fun didPrinterImpossible() {
                    _printerStatus.value = "didPrinterImpossible"
                }

                override fun didAccessoryConnectFailure() {
                    _printerStatus.value = "didAccessoryConnectFailure"
                }

                override fun didPrinterOnline() {
                    _printerStatus.value = "didPrinterOnline"
                }

                override fun didPrinterOffline() {
                    _printerStatus.value = "didPrinterOffline"
                }
            }.freeze()

            connectAsync()
        }
    }

    override fun disconnect() {
        starIoExtManager.value?.disconnect()
        starIoExtManager.value = null
    }

    override suspend fun print(releasePort: Boolean): Boolean {
        starIoExtManager.value?.lock?.lock()
        val result = starIoExtManager.value?.port?.let {
            starSdk.print(port = it as SMPort, releasePort = releasePort)
        } ?: false
        starIoExtManager.value?.lock?.unlock()
       return result
    }
}