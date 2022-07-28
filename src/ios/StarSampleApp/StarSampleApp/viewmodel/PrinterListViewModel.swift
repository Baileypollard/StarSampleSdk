//
//  PrinterListViewModel.swift
//  StarSampleApp
//
//  Created by Bailey Pollard on 2021-11-25.
//

import Foundation
import StarSdk

class PrinterListViewModel: ObservableObject {
    
    private var starManager: StarManager? = nil
    
    @Published
    var printerList: [PortInfo] = []
    
    @Published
    var isSearching: Bool = false
    
    init(starManager: StarManager) {
        self.starManager = starManager
        
        self.starManager?.printer.collect(collector: Collector<[PortInfo]> { portInfoList in
            self.isSearching = false
            self.printerList = portInfoList
        }) { (unit, error) in
            // code which is executed if the Flow object completed
        }
    }
    
    func discoverBluetoothPrinters() {
        printerList = []
        isSearching = true
        starManager?.discoverBluetoothPrinters()
    }
    
    func discoverNetworkPrinters() {
        printerList = []
        isSearching = true
        starManager?.discoverNetworkPrinters()
    }
    
    func discoverAllPrinters() {
        printerList = []
        isSearching = true
        starManager?.discoverAllPrinters()
    }
}

class Collector<T>: Kotlinx_coroutines_coreFlowCollector {
    
    let callback:(T) -> Void
    
    init(callback: @escaping (T) -> Void) {
        self.callback = callback
    }
    
    func emit(value: Any?, completionHandler: @escaping (KotlinUnit?, Error?) -> Void) {
        callback(value as! T)
        completionHandler(KotlinUnit(), nil)
    }
}
