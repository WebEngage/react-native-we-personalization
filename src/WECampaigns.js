import React from 'react';
import { MyLogs } from './MyLogs';
import PersonalizationBridge, { eventEmitter } from './PersonalizationBridge';

let isCampaignListenerAdded = false;
let campaignPreparedListener = null;
let campaignClickedListener = null;
let campaignExceptionListener = null;
let campaignShownListener = null;

export const registerForCampaigns = (campaignCallbackList) => {
  const {
    onCampaignPrepared = null,
    onCampaignShown = null,
    onCampaignClicked = null,
    onCampaignException = null,
  } = campaignCallbackList;

  if (!isCampaignListenerAdded) {
    PersonalizationBridge.registerCampaignCallback();
    if (onCampaignPrepared) {
      campaignPreparedListener = eventEmitter.addListener(
        'onCampaignPrepared',
        (data) => {
          MyLogs('WECampaigns: onCampaignPrepared list', data);
          onCampaignPrepared(data);
        }
      );
    }

    if (onCampaignClicked) {
      campaignClickedListener = eventEmitter.addListener(
        'onCampaignClicked',
        (data) => {
          MyLogs('WECampaigns: onCampaignClicked ', data);
          onCampaignClicked(data);
        }
      );
    }

    if (onCampaignException) {
      campaignExceptionListener = eventEmitter.addListener(
        'onCampaignException',
        (data) => {
          MyLogs('WECampaigns: onCampaignException ', data);
          onCampaignException(data);
        }
      );
    }

    if (onCampaignShown) {
      campaignShownListener = eventEmitter.addListener(
        'onCampaignShown',
        (data) => {
          MyLogs('WECampaigns: onCampaignShown ', data);
          onCampaignShown(data);
        }
      );
      isCampaignListenerAdded = true;
    }
  }
};

export const userWillHandleDeepLink = (doesUserHandleCallbacks) => {
  PersonalizationBridge.userWillHandleDeepLink(doesUserHandleCallbacks);
};

export const unRegisterForCampaigns = () => {
  PersonalizationBridge.unRegisterCampaignCallback();
  campaignPreparedListener?.remove();
  campaignClickedListener?.remove();
  campaignExceptionListener?.remove();
  campaignShownListener?.remove();
  isCampaignListenerAdded = false;
};
