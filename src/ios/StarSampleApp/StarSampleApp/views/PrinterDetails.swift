//
//  PrinterDetails.swift
//  StarSampleApp
//
//  Created by Bailey Pollard on 2021-11-25.
//

import Foundation
import SwiftUI
import StarSdk

struct PrinterDetails: View {
    
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
            
                Button("Print", action: {
                    
                })
            }.padding()
        }.frame(
            minWidth: 0,
            maxWidth: .infinity,
            minHeight: 0,
            maxHeight: .infinity,
            alignment: .topLeading
        ).onAppear(perform: {
            viewModel.connect(portName: portInfo.portName)
        }
        ).onDisappear(perform: {
            viewModel.disconnect()
            viewModel.printerStatus = "Uninitialized"
        })
    }
}


struct PrinterDetails_Previews: PreviewProvider {
    static var previews: some View {
        PrinterDetails(viewModel: PrinterDetailsViewModel(starManager: IosStarManager.companion.create()), portInfo: TestPortInfo())
    }
}

private class TestPortInfo : PortInfo {
    var macAddress: String = "01:00:00:01:01"
    var modelName: String = "TSP100-ASDS"
    var portName: String = "BT:TSP100"
}
