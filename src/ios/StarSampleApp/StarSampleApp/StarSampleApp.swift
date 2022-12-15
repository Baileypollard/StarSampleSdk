//
//  StarSampleApp.swift
//  StarSampleApp
//
//  Created by Bailey Pollard on 2021-11-29.
//

import SwiftUI
import StarSdk
@main
struct PrinterSDK_MiniAppApp: App {
    var body: some Scene {
        let starManager = IosStarManager.companion.create(statusHelper: StarStatusHelper(), wrapper: nativeExtManager)
        WindowGroup {
            PrinterListView(viewModel: PrinterListViewModel(starManager: starManager), starManager: starManager)
        }
    }
}

let nativeExtManager = NativeExtManager()
