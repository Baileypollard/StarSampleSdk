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
import platform.Foundation.NSLog
import platform.darwin.NSObject
import kotlin.native.concurrent.freeze

typealias PrinterStatusListener = (String) -> Unit

/**
 * iOS wrapper around the [StarIoExtManager] provided by the StarSDK.
 * This class allows us to connect, print and observe connection status updates for a
 * given Star Micronics printer.
 */
class IosStarIoExtManagerWrapper(private val starSdk: StarSdk) : StarIoExtManagerWrapper {
    private var starIoExtManager = atomic<StarIoExtManager?>(null)
    private var listener = atomic<PrinterStatusListener?>(null)

//    private val _printerStatus = MutableStateFlow("Uninitialized")
//    override val printerStatus = _printerStatus.asStateFlow()

    override fun getPort(): StarPort {
        // This should never be used, just added to conform to interface
        return starIoExtManager.value?.port as SMPort
    }

    override fun setStatusListener(listener: (String) -> Unit) {
        this.listener.value = listener
        NSLog("[StarIoExt] setting listener, status Uninitialized")
        listener.invoke("Uninitialized")
    }

    override fun connect(portName: String) {
        starIoExtManager.value = StarIoExtManager(StarIoExtManagerTypeStandard, portName, "", 10_000).apply {
            delegate = object : NSObject(), StarIoExtManagerDelegateProtocol {
                override fun manager(manager: StarIoExtManager, didConnectPort: String) {
                    NSLog("[StarIoExt] Initial connection succeeded")
                    listener.value?.invoke("Initial connection succeeded")
                }

                override fun manager(manager: StarIoExtManager, didFailToConnectPort: String, error: NSError?) {
                    NSLog("[StarIoExt] Initial connection failed")
                    listener.value?.invoke("Initial connection failed")
                }

                override fun didPrinterImpossible() {
                    NSLog("[StarIoExt] didPrinterImpossible")
                    listener.value?.invoke("didPrinterImpossible")
                }

                override fun didAccessoryConnectFailure() {
                    NSLog("[StarIoExt] didAccessoryConnectFailure")
                    listener.value?.invoke("didAccessoryConnectFailure")
                }

                override fun didPrinterOnline() {
                    NSLog("[StarIoExt] didPrinterOnline")
                    listener.value?.invoke("didPrinterOnline")
                }

                override fun didPrinterOffline() {
                    NSLog("[StarIoExt] didPrinterOffline")
                    listener.value?.invoke("didPrinterOffline")
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