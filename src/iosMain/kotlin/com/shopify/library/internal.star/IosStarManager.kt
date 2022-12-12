package com.shopify.library.internal.star

/**
 * This is the entry point into the [StarManager] that can be accessed from swift files.
 * As a result, we can call this directly from our frontend iOS code - allowing us to
 * utilize the power of kotlin (coroutines, extension functions etc...) while developing native
 * iOS apps.
 */
class IosStarManager {
    companion object Factory {
        fun create(statusHelper: StatusHelper): StarManager {
            val starSdk = IosStarSdk()
            return StarManager(
                statusHelper = statusHelper,
                starSdk = starSdk,
                starIoExtManagerWrapper = IosStarIoExtManagerWrapper(starSdk),
            )
        }
    }
}