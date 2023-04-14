//
//  PrinterDetails.swift
//  StarSampleApp
//
//  Created by Bailey Pollard on 2021-11-25.
//

import Foundation
import SwiftUI
import StarSdk
import UIKit

struct PrinterDetailsView: View {
    
    @ObservedObject
    var viewModel: PrinterDetailsViewModel
    
    var portInfo: PortInfo
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 10.0) {
                Text(portInfo.modelName).font(.largeTitle)
                Text("MAC address: \(portInfo.macAddress)")
                Text("Port name: \(portInfo.portName)")
                
                if let status = viewModel.printerStatus {
                    Text("Printer status: \(status)")
                }
                HStack(spacing: 20.0) {
                    Button("Print and release port (Wi-Fi Printers)", action: {
                        viewModel.printReceipt(releasePort: true)
                    }).disabled(viewModel.isPrinting || viewModel.printerStatus == "Uninitialized")
                    
                    Button("Print do not release port (Bluetooth Printers)", action: {
                        viewModel.printReceipt(releasePort: false)
                    }).disabled(viewModel.isPrinting || viewModel.printerStatus == "Uninitialized")
                }.padding(.top, 40.0)
                HStack(spacing: 20.0) {
                    Button("Get status (Wi-Fi printers)", action: {
                        viewModel.getWifiPrinterStatus(portInfo: portInfo, timesToReleasePort: 1)
                    })

                    // Hiding this as retriving status works now with releasing port once
                    //  Button("Get status, release port TWICE", action: {
                    //      viewModel.getWifiPrinterStatus(portInfo: portInfo, timesToReleasePort: 2)
                    //  })
                }.padding(.top, 20.0)
            }.padding()
        }.frame(
            minWidth: 0,
            maxWidth: .infinity,
            minHeight: 0,
            maxHeight: .infinity,
            alignment: .topLeading
        ).toolbar(content: {
            ToolbarItemGroup(placement: .navigationBarTrailing, content: {
                HStack(spacing: 20.0) {
                    Button("Connect", action: {
                        viewModel.connect(portName: portInfo.portName)
                    }).disabled(viewModel.isConnecting || viewModel.printerStatus != "Uninitialized")
                    
                    Button("Disconnect", action: {
                        viewModel.disconnect()
                        viewModel.printerStatus = "Uninitialized"
                    }).disabled(viewModel.printerStatus == "Uninitialized")
                }
            })
        }).onDisappear(perform: {
            viewModel.disconnect()
            viewModel.printerStatus = "Uninitialized"
        })
    }
}


struct PrinterDetails_Previews: PreviewProvider {
    static var previews: some View {
        PrinterDetailsView(viewModel: PrinterDetailsViewModel(starManager: IosStarManager.companion.create(), portInfo: TestPortInfo()), portInfo: TestPortInfo())
    }
}

private class TestPortInfo : PortInfo {
    var macAddress: String = "01:00:00:01:01"
    var modelName: String = "TSP100-ASDS"
    var portName: String = "BT:TSP100"
}
