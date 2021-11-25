package com.shopify.library

import com.shopify.library.internal.star.PortInfo

import StarIO.PortInfo as StarPortInfo

internal class IosPortInfo(starPortInfo: StarPortInfo) : PortInfo {
    override val portName: String = starPortInfo.portName() ?: ""
    override val macAddress: String = starPortInfo.macAddress() ?: ""
    override val modelName: String = starPortInfo.modelName() ?: ""
}