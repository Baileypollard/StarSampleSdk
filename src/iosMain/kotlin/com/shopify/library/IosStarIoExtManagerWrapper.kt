package com.shopify.library

import com.shopify.library.internal.star.StarIoExtManagerWrapper
import StarIO_Extension.StarIoExtManager
import StarIO_Extension.StarIoExtManagerDelegateProtocol
import StarIO_Extension.StarIoExtManagerTypeStandard
import StarIO_Extension.connectAsync
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSError
import platform.Foundation.NSLog
import platform.darwin.NSObject
import kotlin.native.concurrent.freeze

class IosStarIoExtManagerWrapper : StarIoExtManagerWrapper {
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
}