package com.shopify.library.internal.star

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect

/**
 * Common class between Android and iOS that can share business logic between both platforms.
 * Writing this code in kotlin allows us to use coroutines on iOS and Android which allows us
 * to easily write async code on both platforms.
 */
class StarManager(
    private val starSdk: StarSdk,
    private val starIoExtManagerWrapper: StarIoExtManagerWrapper,
    private val backgroundScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) {

    private val _printers = MutableSharedFlow<List<PortInfo>>()
    val printer = _printers.asSharedFlow()

    private val _printResult = MutableSharedFlow<Boolean>()
    val printResult = _printResult.asSharedFlow()

    private val _printerStatus = MutableSharedFlow<String>()
    val printerStatus = _printerStatus.asSharedFlow()

    init {
        backgroundScope.launch {
            starIoExtManagerWrapper.printerStatus.collect { status ->
                _printerStatus.emit(status)
            }
        }
    }

    fun discoverBluetoothPrinters() {
        backgroundScope.launch {
            starSdk.searchPrinters(StarSdk.StarQuery.Bluetooth).also {
                _printers.emit(it)
            }
        }
    }

    fun discoverNetworkPrinters() {
        backgroundScope.launch {
            starSdk.searchPrinters(StarSdk.StarQuery.Network).also {
                _printers.emit(it)
            }
        }
    }

    fun discoverAllPrinters() {
        backgroundScope.launch {
            starSdk.searchPrinters(StarSdk.StarQuery.All).also {
                _printers.emit(it)
            }
        }
    }

    fun connect(portName: String) {
        starIoExtManagerWrapper.connect(portName = portName)
    }

    fun disconnect() {
        starIoExtManagerWrapper.disconnect()
    }

    fun print(releasePort: Boolean) {
        backgroundScope.launch {
            starIoExtManagerWrapper.print(releasePort = releasePort).also {
                _printResult.emit(it)
            }
        }
    }

    fun getWifiPrinterStatus(portInfo: PortInfo, timesToReleasePort: Int) {
        backgroundScope.launch {
            _printerStatus.emit("Retrieving...")
            val status = starSdk.getWifiPrinterStatus(portInfo, timesToReleasePort)
            _printerStatus.emit(status)
        }
    }

    fun releasePort() {
        backgroundScope.launch { starSdk.releasePort() }
    }
}