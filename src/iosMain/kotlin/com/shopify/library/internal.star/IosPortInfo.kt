package com.shopify.library.internal.star

import StarIO.SMPort
import StarIO.PortInfo as StarPortInfo

actual typealias Port = SMPort

class IosPortInfo(starPortInfo: StarPortInfo) : PortInfo {
    override val portName: String = starPortInfo.portName() ?: ""
    override val macAddress: String = starPortInfo.macAddress() ?: ""
    override val modelName: String = starPortInfo.modelName() ?: ""
}