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
                Text(portInfo.macAddress)
                Text(portInfo.portName)
                
                if let status = viewModel.printerStatus {
                    Text(status)
                }
                HStack(spacing: 20.0) {
                    Button("Print and release port", action: {
                        viewModel.print(releasePort: true)
                    }).disabled(viewModel.isPrinting || viewModel.printerStatus == "Uninitialized")
                    
                    Button("Print do not release port", action: {
                        viewModel.print(releasePort: false)
                    }).disabled(viewModel.isPrinting || viewModel.printerStatus == "Uninitialized")
                }
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
        PrinterDetailsView(viewModel: PrinterDetailsViewModel(starManager: IosStarManager.companion.create()), portInfo: TestPortInfo())
    }
}

private class TestPortInfo : PortInfo {
    var macAddress: String = "01:00:00:01:01"
    var modelName: String = "TSP100-ASDS"
    var portName: String = "BT:TSP100"
}
