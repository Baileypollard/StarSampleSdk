//
//  PrinterDetailsViewModel.swift
//  StarSampleApp
//
//  Created by Bailey Pollard on 2021-11-26.
//

import Foundation
import StarSdk
import UIKit

class PrinterDetailsViewModel: ObservableObject {
    
    private var starManager: StarManager? = nil
	private var portInfo: PortInfo? = nil
    
    @Published
    var printerStatus: String = "Uninitialized"
    
    @Published
    var isPrinting: Bool = false
    
    @Published
    var isConnecting: Bool = false
    
	init(starManager: StarManager, portInfo: PortInfo) {
        self.starManager = starManager
		self.portInfo = portInfo
        onViewDidLoad()
        self.starManager?.printerStatus.collect(collector: Collector<String> { status in
            self.isConnecting = false
            self.printerStatus = status
        }) { (unit, error) in
            // code which is executed if the Flow object completed
        }
        
        self.starManager?.printResult.collect(collector: Collector<Bool> { result in
            self.isPrinting = false
        }) { (unit, error) in
            // code which is executed if the Flow object completed
        }
    }

    func connect(portName: String) {
        isConnecting = true
        starManager?.connect(portName: portName)
    }
    
    func disconnect() {
        starManager?.disconnect()
    }
    
    func printReceipt(releasePort: Bool) {
        isPrinting = true
        starManager?.print(releasePort: releasePort)
    }

    func getWifiPrinterStatus(portInfo: PortInfo, timesToReleasePort: Int32) {
        starManager?.getWifiPrinterStatus(portInfo: portInfo, timesToReleasePort: timesToReleasePort)
    }
	
	func onViewDidLoad() {
		let notificationCenter = NotificationCenter.default
		notificationCenter.addObserver(self, selector: #selector(appMovedToBackground), name: UIApplication.didEnterBackgroundNotification, object: nil)
		notificationCenter.addObserver(self, selector: #selector(appMovedToForeground), name: UIApplication.willEnterForegroundNotification, object: nil)
	}
	
	@objc func appMovedToBackground() {
		disconnect()
	}
	
	@objc func appMovedToForeground() {
		connect(portName: portInfo?.portName ?? "")
	}
}
