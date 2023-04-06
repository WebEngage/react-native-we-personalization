import React from 'react';
import { MyLogs } from '../utils/MyLogs';
import WEPersonalizationBridge, { eventEmitter } from '../bridge/WEPersonalizationBridge';

let isCampaignListenerAdded = false;
let campaignPreparedListener = null;
let campaignClickedListener = null;
let campaignExceptionListener = null;
let campaignShownListener = null;

export const registerWECampaignCallback = (campaignCallbackList) => {
  const {
    onCampaignPrepared = null,
    onCampaignShown = null,
    onCampaignClicked = null,
    onCampaignException = null,
  } = campaignCallbackList;

  if (!isCampaignListenerAdded) {
    WEPersonalizationBridge.registerCampaignCallback();
    if (onCampaignPrepared) {
      campaignPreparedListener = eventEmitter.addListener(
        'onCampaignPrepared',
        (WECampaignData) => {
          MyLogs('WECampaigns: onCampaignPrepared list', WECampaignData);
          onCampaignPrepared(WECampaignData);
        }
      );
    }

    if (onCampaignClicked) {
      campaignClickedListener = eventEmitter.addListener(
        'onCampaignClicked',
        (WECampaignData) => {
          MyLogs('WECampaigns: onCampaignClicked ', WECampaignData);
          onCampaignClicked(WECampaignData);
        }
      );
    }

    if (onCampaignException) {
      campaignExceptionListener = eventEmitter.addListener(
        'onCampaignException',
        (WECampaignData) => {
          MyLogs('WECampaigns: onCampaignException ', WECampaignData);
          onCampaignException(WECampaignData);
        }
      );
    }

    if (onCampaignShown) {
      campaignShownListener = eventEmitter.addListener(
        'onCampaignShown',
        (WECampaignData) => {
          MyLogs('WECampaigns: onCampaignShown ', WECampaignData);
          onCampaignShown(WECampaignData);
        }
      );
      isCampaignListenerAdded = true;
    }
  }
};

export const deregisterWECampaignCallback = () => {
  WEPersonalizationBridge.unRegisterCampaignCallback();
  campaignPreparedListener?.remove();
  campaignClickedListener?.remove();
  campaignExceptionListener?.remove();
  campaignShownListener?.remove();
  isCampaignListenerAdded = false;
};
