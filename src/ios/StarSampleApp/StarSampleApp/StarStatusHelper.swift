//
//  StarStatusHelper.swift
//  StarSampleApp
//
//  Created by Philip Shadlyn on 2022-12-12.
//

import Foundation
import StarSdk

class StarStatusHelper: StatusHelper {
    func getStatus(portName: String) -> String {
        var port: SMPort? = nil
        var status: String = "Unknown"
                
        do {
            port = try SMPort.getPort(portName: portName, portSettings: "", ioTimeoutMillis: 10000)
            var starStatus = StarPrinterStatus_2()
            try port?.getParsedStatus(starPrinterStatus: &starStatus, level: 2)
                        
            if starStatus.offline == 0 {
                status = "Online"
            } else {
                status = "Offline"
            }
        } catch {
            // error
            status = "error..."
        }
        
        SMPort.release(port)
        return status
    }
}
