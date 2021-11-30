package com.shopify.library.internal.star

import StarIO.*
import StarIO_Extension.ISCBBuilder
import StarIO_Extension.SCBCutPaperAction
import StarIO_Extension.SCBPrintableAreaType
import StarIO_Extension.StarIoExt
import StarIO_Extension.StarIoExtEmulationStarGraphic
import kotlinx.cinterop.*
import platform.Foundation.NSError
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.ptr
import kotlinx.cinterop.set
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.Foundation.NSBundle
import platform.Foundation.NSLog
import platform.Foundation.NSMutableData
import platform.UIKit.UIImage
import platform.posix.memcpy
import platform.posix.u_int8_tVar

/**
 * iOS's implementation of the [StarSdk] that allows us to interact directly with the [StarIO] SDK.
 * Provides us with the functionality needed to discover printers,
 * retrieve status updates and print our sample receipt
 */
internal class IosStarSdk : StarSdk {
    override suspend fun searchPrinters(target: StarSdk.StarQuery): List<PortInfo> {
        return SMPort.searchPrinter(target.query)
            ?.filterIsInstance<StarIO.PortInfo>()
            ?.map { IosPortInfo(it) }
            ?: emptyList()
    }

    override suspend fun print(port: SMPort, releasePort: Boolean): Boolean {
        NSLog("[IosStarSdk] printing...")
        memScoped {
            val checkedBlockError = alloc<ObjCObjectVar<NSError?>>()
            try {
                val printerStatus = alloc<StarPrinterStatus_2_>()
                port.beginCheckedBlock(printerStatus.ptr, 2, checkedBlockError.ptr)
                if (printerStatus.offline == SM_TRUE) {
                    NSLog("[IosStarSdk] print failed - printer offline")
                    return false
                }

                val builder = StarIoExt.createCommandBuilder(StarIoExtEmulationStarGraphic)
                val commands = builder?.createPrintRequest() ?: byteArrayOf(0x41)

                val buffer = createBuffer(content = commands)

                var bytesWritten: UInt = 0u
                val bytesToWrite: UInt = commands.size.toUInt()

                while (bytesWritten < bytesToWrite) {
                    val size = bytesToWrite - bytesWritten
                    val totalWritten = port.writePort(buffer, bytesWritten, size)

                    bytesWritten += totalWritten
                }
                NSLog("[IosStarSdk] print success")
                port.endCheckedBlock(printerStatus.ptr, 2, checkedBlockError.ptr)
                return true
            } catch (e: Exception) {
                NSLog("[IosStarSdk] print failed $e")
                return false
            } finally {
                try {
                    if (releasePort) {
                        SMPort.releasePort(port)
                    }
                } catch (e: Exception) {
                    NSLog("[IosStarSdk] Release port failed $e")
                }
            }
        }
    }

    override suspend fun getWifiPrinterStatus(portInfo: PortInfo, timesToReleasePort: Int): String {
        memScoped {
            val portConnectionError = alloc<ObjCObjectVar<NSError?>>()
            val port = SMPort.getPort(
                portInfo.portName,
                ";l5000",
                10000.toUInt(),
                portConnectionError.ptr
            )
            return if (port != null) {
                // get printer status
                val status = alloc<StarPrinterStatus_2_>()
                port.getParsedStatus(status.ptr, 2)

                // release port
                repeat(timesToReleasePort) {
                    SMPort.releasePort(port)
                }

                // return status of either "Online" or "Offline" to caller
                if (status.offline == SM_TRUE) {
                    NSLog("[IosStarSdk] Printer is OFFLINE")
                    "Offline"
                } else {
                    NSLog("[IosStarSdk] Printer is ONLINE")
                    "Online"
                }
            } else {
                NSLog("[IosStarSdk] Failed to open printer port, with an error of: ${portConnectionError.value}")
                "Offline"
            }
        }
    }

    // Creates the print request with the given image
    private fun ISCBBuilder.createPrintRequest(): ByteArray {
        beginDocument()
        appendPrintableArea(SCBPrintableAreaType.SCBPrintableAreaTypeStandard)
        createPrintImage().also {
            val imageWidth = it.size.useContents { this.width }
            appendBitmap(it, false, imageWidth.toLong(), true)
        }
        appendCutPaper(SCBCutPaperAction.SCBCutPaperActionFullCutWithFeed)
        endDocument()

        return commands?.toByteArray() ?: ByteArray(0)
    }

    // Fetches our sample image from resources
    private fun createPrintImage(): UIImage {
        return UIImage.imageNamed(
            name = "kermit",
            inBundle = NSBundle.mainBundle,
            compatibleWithTraitCollection = null
        ) ?: throw Exception("kermit.jpeg file not found in the main iOS bundle")
    }

    private fun NativePlacement.createBuffer(content: ByteArray): CArrayPointer<u_int8_tVar> {
        val buffer = allocArray<u_int8_tVar>(content.size)
        var bufferIndex = 0
        content.forEach {
            buffer[bufferIndex] = it.toUByte()
            bufferIndex += 1
        }
        return buffer
    }

    private fun NSMutableData.toByteArray(): ByteArray = ByteArray(this@toByteArray.length.toInt()).apply {
        usePinned {
            memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
        }
    }
}