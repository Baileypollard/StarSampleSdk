package com.shopify.library.internal.star

import StarIO.PortInfo as StarPortInfo

class IosPortInfo(starPortInfo: StarPortInfo) : KmmPortInfo {
    override val portName: String = starPortInfo.portName() ?: ""
    override val macAddress: String = starPortInfo.macAddress() ?: ""
    override val modelName: String = starPortInfo.modelName() ?: ""
}