package com.shopify.library

import com.shopify.library.internal.star.StarManager

class IosStarManager {
    companion object Factory {
        fun create(): StarManager {
            return StarManager(starSdk = IosStarSdk())
        }
    }
}