package com.shopify.library.internal.star

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class StarManager(
    private val starSdk: StarSdk,
    private val backgroundScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

    private val _printers = MutableSharedFlow<List<PortInfo>>()
    val printer = _printers.asSharedFlow()

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
}