# StarSampleSdk

## iOS Bugs

#### Star Bluetooth printer with an active StarIoExtManager connection won't reconnect after calling `disconnect()` when app is backgrounded and `connect()` when app is foregrounded.

  **Bug description:**
This sample app will use a `StarIoExtManager` to connect with a Bluetooth printer.  Once connected, the app will automatically call `starIoExtManager.disconnect()` if the app is subsequently backgrounded, and then `starIoExtManager.connect()` if the app is brought into the foreground again.  However if the printer is switched off while the app is backgrounded, and then switched on _after_ the app has been foregrounded again, the `StarIoExtManager` will fail to reconnect to the printer and report the following error:

<insert error message from Star Micronics SDK>

The only way to get the printer to connect to the sample app again is to force close and restart the app.
  **Steps to reproduce:**
  1. Manually pair a Star Micronics Bluetooth printer to the iOS device
  2. On the sample app, click the "Scan BT" button to discover the paired BT printer
  3. Click on the discovered BT printer row to navigate to printer details
  4. In the top right, click "Connect" - wait for the connection to succeed 
  5. When the connection status is "didPrinterOnline", put the sample app into
     the background by pressing the home button on the iOS device
  6. Turn off the connected BT Printer
  7. Bring the sample back into the foreground, printer status now displays "Initial connection failed"
  8. Turn back on BT printer, printer status still displays "Initial connection failed"
  9. The only way to get the printer to connect to the sample app again is to force close and restart the app

  **Expected outcome:**
When backgrounding the sample app, turning off the printer, and then opening the app again, the app should be able to reconnect to the printer once it is switched on again.
  
  **Actual outcome:**
The printer never reconnects to the sample app after powering on. An app restart is needed to get it to connect again.

--------------------------------------  
  
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

