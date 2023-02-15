import React from 'react';
import PersonalizationBridge, { eventEmitter } from './PersonalizationBridge';

let isCampaignListenerAdded = false;
let campaignPreparedListener = null;
let campaignClickedListener = null;
let campaignExceptionListener = null;
let campaignShownListener = null;

export const registerForCampaigns = (campaignCallbackList) => {
  console.log('campaignCallbackList - ', campaignCallbackList);
  const {
    onCampaignPrepared = null,
    onCampaignShown = null,
    onCampaignClicked = null,
    onCampaignException = null,
  } = campaignCallbackList;

  if (!isCampaignListenerAdded) {
    console.log('registerForCampaigns ', isCampaignListenerAdded);
    PersonalizationBridge.registerCampaignCallback();
    if (onCampaignPrepared) {
      campaignPreparedListener = eventEmitter.addListener(
        'onCampaignPrepared',
        (data) => {
          console.log('WECa: onCampaignPrepared list', data);
          onCampaignPrepared(data);
        }
      );
    }

    if (onCampaignClicked) {
      campaignClickedListener = eventEmitter.addListener(
        'onCampaignClicked',
        (data) => {
          console.log('WECa: onCampaignClicked list', data);
          onCampaignClicked(data);
        }
      );
    }

    if (onCampaignException) {
      campaignExceptionListener = eventEmitter.addListener(
        'onCampaignException',
        (data) => {
          console.log('WECa: onCampaignException list', data);
          onCampaignException(data);
        }
      );
    }
  }

  if (onCampaignShown) {
    campaignShownListener = eventEmitter.addListener(
      'onCampaignShown',
      (data) => {
        console.log('WECa: onCampaignShown list', data);
        onCampaignShown(data);
      }
    );
    isCampaignListenerAdded = true;
  }
};

export const unRegisterForCampaigns = () => {
  PersonalizationBridge.unRegisterCampaignCallback();
  campaignPreparedListener?.remove();
  campaignClickedListener?.remove();
  campaignExceptionListener?.remove();
  campaignShownListener?.remove();
  isCampaignListenerAdded = false;
};
