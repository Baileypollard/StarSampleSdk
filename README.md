# StarSampleSdk

## iOS Bugs that need to be reproduced

1. Retrieving network printer status fails after subsequent attempts

2. Star Bluetooth printer with an active StarIoExtManager connection won't reconnect if we don't call releasePort() after a print job. As a result, calling releasePort()
results in the EXC_BAD_ACCESS crashes that we have been seeing in production

3. Star Bluetooth printer with an active StarIoExtManager connection won't reconnect after calling disconnect()/connect()

4. Star mPOP printer has no macAddress in PortInfo
