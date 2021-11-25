//
//  ContentView.swift
//  StarSampleApp
//
//  Created by Bailey Pollard on 2021-11-24.
//

import SwiftUI
import StarSdk

struct PrinterListView: View {
    
    @ObservedObject
    var viewModel: PrinterListViewModel
    
    var body: some View {
        NavigationView {
            VStack(alignment: .leading) {
                HStack(spacing: 15.0) {
                    Button("Scan BT", action: {
                        viewModel.discoverBluetoothPrinters()
                    })
                    Button("Scan Network", action: {
                        viewModel.discoverNetworkPrinters()
                    })
                }.padding()
                if let list = viewModel.printerList {
                    List(list, id: \.macAddress) { portInfo in
                        VStack(alignment: .leading) {
                            Text(portInfo.modelName)
                            Text(portInfo.macAddress)
                            Text(portInfo.portName)
                        }
                    }
                    .navigationTitle("Printers")
                }
            }
        }.navigationViewStyle(StackNavigationViewStyle())
    }
}

struct PrinterListView_Previews: PreviewProvider {
    static var previews: some View {
        PrinterListView(viewModel: PrinterListViewModel(starManager: IosStarManager.companion.create()))
    }
}
