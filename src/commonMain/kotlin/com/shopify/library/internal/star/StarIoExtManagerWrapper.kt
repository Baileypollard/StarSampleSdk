package com.shopify.library.internal.star

import kotlinx.coroutines.flow.StateFlow

interface StarIoExtManagerWrapper {
    val printerStatus: StateFlow<String>

    fun connect(portName: String)
    fun disconnect()
}