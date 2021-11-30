# StarSampleSdk

## iOS Bugs that need to be reproduced

1. Retrieving network printer status fails after subsequent attempts

2. Star Bluetooth printer with an active StarIoExtManager connection won't reconnect if we don't call releasePort() after a print job. As a result, calling releasePort()
results in the EXC_BAD_ACCESS crashes that we have been seeing in production

3. Star Bluetooth printer with an active StarIoExtManager connection won't reconnect after calling disconnect()/connect()

4. Star mPOP printer has an empty macAddress in PortInfo object

## Background Context

Native iOS app location: **src/ios/StarSampleApp.xcodeproj**

Notice that inside the src/ directory we also have androidMain/ iosMain/ and commonMain/ directories. These are apart of Kotlin Multiplatform (https://kotlinlang.org/docs/kmm-overview.html) and allow is to share common business logic between both iOS and Android platforms. Both the iosMain/ and androidMain/ will contain platform specific code - such as access to the StarSdk. While the commonMain/ directory contains code that isn't specific to any operating system. Meaning that we can share the code between platforms. This allows us to have constant feature pairity between both iOS and Android while minimizing the need for two separate dev teams ("iOS/Android devs" become "Mobile devs")

## Set up

### Xcode 13 and an iOS device running atleast iOS 15 will be required. 

You can open the iOS app by navigating to the src/ios/StarSampleApp/ directory and opening the StarSampleApp.xcodeproj file. 

Once you have the project opened in Xcode, assign a signing team and build + run the app on a physical device.

If you happen to see this error, then append the bundle id with some random characters such as "Shopify.StarSampleApp.cxs".

<img width="720" alt="Screen Shot 2021-11-30 at 12 41 02 PM" src="https://user-images.githubusercontent.com/45129610/144099415-59f9323f-3190-4e98-85ad-414a1c01394a.png">

Another error that could appear is below

<img width="540" alt="Screen Shot 2021-11-30 at 12 42 18 PM" src="https://user-images.githubusercontent.com/45129610/144099609-7c73b85e-7898-47df-b280-ce5cb72b1e51.png">

This happens when you first build the framework for a simulator, and then go to run it on a physical device (or vice versa). To fix this, navigate to the build/bin/ios/debugFramework directory and remove both the StarSdk.framework and the StarSdk.framework.dSYM files and rebuild the project. 

