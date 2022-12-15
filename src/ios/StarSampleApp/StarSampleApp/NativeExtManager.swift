//
//  NativeExtManager.swift
//  StarSampleApp
//
//  Created by Philip Shadlyn on 2022-12-14.
//

import Foundation
import StarSdk

class NativeExtManager: StarIoExtManagerWrapper {

    private var listener: ((String) -> Void)? = nil
    private var starIoExtManager: StarIoExtManager? = nil
    private var starDelegate: StarDelegate? = nil
    
    init() {
        
    }

    func setStatusListener(listener: @escaping (String) -> Void) {
        Swift.print("[NativeExtManager] called setStatusListener")
        self.listener = listener
    }
    
    func connect(portName: String) {
        Swift.print("[NativeExtManager] called CONNECT")
        starIoExtManager = StarIoExtManager(type: StarIoExtManagerType.standard, portName: portName, portSettings: "", ioTimeoutMillis: 10000)
        
        self.starDelegate = StarDelegate(listener: listener!)
        starIoExtManager?.delegate = self.starDelegate
        
        starIoExtManager?.connectAsync()
    }
    
    func disconnect() {
        Swift.print("[NativeExtManager] called DISCONNECT")
        starIoExtManager?.disconnect()
        starIoExtManager = nil
        starDelegate = nil
    }
    
    func print(releasePort: Bool, completionHandler: @escaping (KotlinBoolean?, Error?) -> Void) {
        Swift.print("[NativeExtManager] called PRINT")
    }
    
    private class StarDelegate: NSObject, StarIoExtManagerDelegate {
     
        private var listener: (String) -> Void
        
        init(listener: @escaping (String) -> Void) {
            self.listener = listener
        }
        
        func manager(_ manager: StarIoExtManager, didConnectPort portName: String) {
            listener("Initial connection succeeded")
        }
        
        func manager(_ manager: StarIoExtManager, didFailToConnectPort portName: String, error: Error?) {
            listener("Initial connection failed")
        }
        
        func didPrinterOnline() {
            listener("didPrinterOnline")
        }
        
        func didPrinterOffline() {
            listener("didPrinterOffline")
        }
        
        func didPrinterImpossible() {
            listener("didPrinterImpossible")
        }
        
        func didAccessoryConnectFailure() {
            listener("didAccessoryConnectFailure")
        }
    }
}
