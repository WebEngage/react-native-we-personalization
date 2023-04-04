//
//  WEGHInline.swift
//  BEMCheckBox
//
//  Created by Akshaykumar Chilad on 16/03/23.
//

import Foundation
import WEPersonalization

public class WEGHInline{
    var id:Int
    var screenName:String
    var propertyID:Int
    var campaignData : WECampaignData? = nil
    public init(id: Int, screenName: String, propertyID: Int, campaignData: WECampaignData? = nil) {
        self.id = id
        self.screenName = screenName
        self.propertyID = propertyID
        self.campaignData = campaignData
    }
}

