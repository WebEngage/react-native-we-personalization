import {weLogs} from '../utils/weLogs';
import
WEPersonalizationBridge,
{eventEmitter} from '../bridge/WEPersonalizationBridge';

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
    WEPersonalizationBridge.registerWECampaignCallback();
    if (onCampaignPrepared) {
      campaignPreparedListener = eventEmitter.addListener(
          'onCampaignPrepared',
          (WECampaignData) => {
            weLogs('WECampaigns: onCampaignPrepared list', WECampaignData);
            onCampaignPrepared(WECampaignData);
          },
      );
    }

    if (onCampaignClicked) {
      console.log('WECampaigns: Registering onCampaignClicked listener');
      campaignClickedListener = eventEmitter.addListener(
          'onCampaignClicked',
          (WECampaignData) => {
            console.log('WECampaigns: onCampaignClicked event received', WECampaignData);
            weLogs('WECampaigns: onCampaignClicked ', WECampaignData);
            onCampaignClicked(WECampaignData);
          },
      );
    }

    if (onCampaignException) {
      campaignExceptionListener = eventEmitter.addListener(
          'onCampaignException',
          (WECampaignData) => {
            weLogs('WECampaigns: onCampaignException ', WECampaignData);
            onCampaignException(WECampaignData);
          },
      );
    }

    if (onCampaignShown) {
      campaignShownListener = eventEmitter.addListener(
          'onCampaignShown',
          (WECampaignData) => {
            weLogs('WECampaigns: onCampaignShown ', WECampaignData);
            onCampaignShown(WECampaignData);
          },
      );
    }
    isCampaignListenerAdded = true;
  }
};

export const deregisterWECampaignCallback = () => {
  WEPersonalizationBridge.deregisterWECampaignCallback();
  campaignPreparedListener?.remove();
  campaignClickedListener?.remove();
  campaignExceptionListener?.remove();
  campaignShownListener?.remove();
  isCampaignListenerAdded = false;
};
