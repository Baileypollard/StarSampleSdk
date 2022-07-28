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
    
    var starManager: StarManager
    
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
                    Button("Scan All", action: {
                        viewModel.discoverAllPrinters()
                    })
                }.padding()
                if let list = viewModel.printerList {
                    List(list, id: \.macAddress) { portInfo in
                        NavigationLink(destination: PrinterDetailsView(viewModel: PrinterDetailsViewModel(starManager: starManager), portInfo: portInfo)) {
                            VStack(alignment: .leading) {
                                Text(portInfo.modelName)
                                Text(portInfo.macAddress)
                                Text(portInfo.portName)
                            }
                        }
                    }
                    .overlay(Group {
                        if list.isEmpty {
                            viewModel.isSearching ? Text("Discovering printers...") : Text("No printers found")
                        }
                    })
                    .navigationTitle("Printers")
                }
            }
        }.navigationViewStyle(StackNavigationViewStyle())
    }
}

struct PrinterListView_Previews: PreviewProvider {
    static var previews: some View {
        let starManager = IosStarManager.companion.create()
        PrinterListView(viewModel: PrinterListViewModel(starManager: starManager), starManager: starManager)
    }
}
