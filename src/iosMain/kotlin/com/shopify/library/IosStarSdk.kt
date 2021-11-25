package com.shopify.library

import StarIO.SMPort
import com.shopify.library.internal.star.PortInfo
import com.shopify.library.internal.star.StarSdk

internal class IosStarSdk : StarSdk {
    override suspend fun searchPrinters(target: StarSdk.StarQuery): List<PortInfo> {
        return SMPort.searchPrinter(target.query)
            ?.filterIsInstance<StarIO.PortInfo>()
            ?.map { IosPortInfo(it) }
            ?: emptyList()
    }
}