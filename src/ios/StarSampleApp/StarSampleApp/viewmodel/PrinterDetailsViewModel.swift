//
//  PrinterDetailsViewModel.swift
//  StarSampleApp
//
//  Created by Bailey Pollard on 2021-11-26.
//

import Foundation
import StarSdk

class PrinterDetailsViewModel: ObservableObject {
    
    private var starManager: StarManager? = nil
    
    @Published
    var printerStatus: String = "Uninitialized"
    
    init(starManager: StarManager) {
        self.starManager = starManager
        
        self.starManager?.printerStatus.collect(collector: Collector<String> { status in
            self.printerStatus = status
        }) { (unit, error) in
            // code which is executed if the Flow object completed
        }
    }

    func connect(portName: String) {
        starManager?.connect(portName: portName)
    }
    
    func disconnect() {
        starManager?.disconnect()
    }
}


