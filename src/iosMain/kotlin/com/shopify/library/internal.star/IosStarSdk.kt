package com.shopify.library.internal.star

import StarIO.SMPort
import StarIO.SM_TRUE
import StarIO.StarPrinterStatus_2_
import StarIO_Extension.ISCBBuilder
import StarIO_Extension.SCBCutPaperAction
import StarIO_Extension.SCBPrintableAreaType
import StarIO_Extension.StarIoExt
import StarIO_Extension.StarIoExtEmulationStarGraphic
import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.NativePlacement
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.memScoped
import platform.Foundation.NSError
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.ptr
import kotlinx.cinterop.set
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGFloat
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSBundle
import platform.Foundation.NSLog
import platform.Foundation.NSMutableData
import platform.UIKit.UIGraphicsBeginImageContext
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.posix.memcpy
import platform.posix.u_int8_tVar

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