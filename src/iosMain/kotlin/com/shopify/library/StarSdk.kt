package com.shopify.library

import StarIO.SMPort
import platform.Foundation.NSLog

class StarSdk {
    fun discoverBtPrinters() {
        SMPort.searchPrinter("BT:").also {
            NSLog("Found ${it?.size} printers")
        }
    }
}