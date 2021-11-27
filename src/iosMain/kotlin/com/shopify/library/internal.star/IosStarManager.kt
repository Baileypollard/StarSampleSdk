package com.shopify.library.internal.star

class IosStarManager {
    companion object Factory {
        fun create(): StarManager {
            val starSdk = IosStarSdk()
            return StarManager(
                starSdk = starSdk,
                starIoExtManagerWrapper = IosStarIoExtManagerWrapper(starSdk),
            )
        }
    }
}