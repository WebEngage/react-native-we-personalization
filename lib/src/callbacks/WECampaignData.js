import { weLogs } from '../utils/weLogs';
import WEPersonalizationBridge, { eventEmitter } from '../bridge/WEPersonalizationBridge';

let isCampaignListenerAdded = false;
const listeners = {
  campaignPrepared: null,
  campaignClicked: null,
  campaignException: null,
  campaignShown: null,
};

/**
 * Register campaign callbacks
 * @param {Object} campaignCallbackList - Campaign callback handlers
 */
export const registerWECampaignCallback = (campaignCallbackList = {}) => {
  if (!campaignCallbackList || typeof campaignCallbackList !== 'object') {
    weLogs('WARN: Invalid campaign callback list provided');
    return;
  }

  if (!eventEmitter) {
    weLogs('ERROR: Event emitter not available');
    return;
  }

  const {
    onCampaignPrepared,
    onCampaignShown,
    onCampaignClicked,
    onCampaignException,
  } = campaignCallbackList;

  if (isCampaignListenerAdded) {
    weLogs('WARN: Campaign listeners already registered');
    return;
  }

  try {
    WEPersonalizationBridge?.registerWECampaignCallback();

    if (onCampaignPrepared && typeof onCampaignPrepared === 'function') {
      listeners.campaignPrepared = eventEmitter.addListener(
        'onCampaignPrepared',
        (data) => {
          try {
            weLogs('WECampaigns: onCampaignPrepared', data);
            onCampaignPrepared(data);
          } catch (error) {
            weLogs('ERROR: onCampaignPrepared callback error:', error);
          }
        }
      );
    }

    if (onCampaignClicked && typeof onCampaignClicked === 'function') {
      listeners.campaignClicked = eventEmitter.addListener(
        'onCampaignClicked',
        (data) => {
          try {
            weLogs('WECampaigns: onCampaignClicked', data);
            onCampaignClicked(data);
          } catch (error) {
            weLogs('ERROR: onCampaignClicked callback error:', error);
          }
        }
      );
    }

    if (onCampaignException && typeof onCampaignException === 'function') {
      listeners.campaignException = eventEmitter.addListener(
        'onCampaignException',
        (data) => {
          try {
            weLogs('WECampaigns: onCampaignException', data);
            onCampaignException(data);
          } catch (error) {
            weLogs('ERROR: onCampaignException callback error:', error);
          }
        }
      );
    }

    if (onCampaignShown && typeof onCampaignShown === 'function') {
      listeners.campaignShown = eventEmitter.addListener(
        'onCampaignShown',
        (data) => {
          try {
            weLogs('WECampaigns: onCampaignShown', data);
            onCampaignShown(data);
          } catch (error) {
            weLogs('ERROR: onCampaignShown callback error:', error);
          }
        }
      );
    }

    isCampaignListenerAdded = true;
  } catch (error) {
    weLogs('ERROR: registerWECampaignCallback failed:', error);
  }
};

/**
 * Deregister campaign callbacks and clean up listeners
 */
export const deregisterWECampaignCallback = () => {
  if (!isCampaignListenerAdded) {
    weLogs('WARN: No campaign listeners to deregister');
    return;
  }

  try {
    WEPersonalizationBridge?.deregisterWECampaignCallback();

    Object.values(listeners).forEach((listener) => {
      try {
        listener?.remove();
      } catch (error) {
        weLogs('ERROR: Failed to remove listener:', error);
      }
    });

    listeners.campaignPrepared = null;
    listeners.campaignClicked = null;
    listeners.campaignException = null;
    listeners.campaignShown = null;

    isCampaignListenerAdded = false;
  } catch (error) {
    weLogs('ERROR: deregisterWECampaignCallback failed:', error);
  }
};
