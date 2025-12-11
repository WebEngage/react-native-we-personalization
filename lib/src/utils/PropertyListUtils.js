import { weLogs } from './weLogs';

/**
 * Register a property in the property list
 * @param {Array} list - Property list
 * @param {string} screenName - Screen name
 * @param {string} propertyId - Property ID
 * @param {Function|null} onDataReceived - Data received callback
 * @param {Function|null} onRendered - Rendered callback
 * @param {Function|null} onPlaceholderException - Exception callback
 * @returns {Array} Updated list
 */
export const registerPropertyList = (
  list = [],
  screenName,
  propertyId,
  onDataReceived = null,
  onRendered = null,
  onPlaceholderException = null
) => {
  if (!list || !Array.isArray(list)) {
    weLogs('ERROR: registerPropertyList - invalid list provided');
    return [];
  }

  if (!screenName || !propertyId) {
    weLogs('ERROR: registerPropertyList - screenName and propertyId are required');
    return list;
  }

  const screenIndex = getLatestScreenIndex(screenName, list);
  if (screenIndex === -1) {
    list.push({
      screenName,
      propertyList: [],
    });
  }

  const lastScreen = list[list.length - 1];
  if (lastScreen && lastScreen.propertyList) {
    const existingPropertyIds = lastScreen.propertyList.map((curr) => curr.propertyId);
    if (!existingPropertyIds.includes(propertyId)) {
      lastScreen.propertyList.push({
        propertyId,
        callbacks: {
          onRendered,
          onPlaceholderException,
          onDataReceived,
        },
      });
    }
  }

  weLogs('PropertyListUtils: screenList updated', list);
  return list;
};

/**
 * Get the index of a screen in the list
 * @param {string} screen - Screen name
 * @param {Array} list - Property list
 * @returns {number} Screen index or -1 if not found
 */
export function getLatestScreenIndex(screen, list = []) {
  if (!screen || !list || !Array.isArray(list)) {
    return -1;
  }
  return list.findIndex((obj) => obj && obj.screenName === screen);
}

/**
 * Remove a property from the property list
 * @param {Array} list - Property list
 * @param {string} screenName - Screen name
 * @param {string} propertyId - Property ID
 * @param {Array} listenerList - Listener list
 * @param {boolean} listenerFlag - Listener flag
 * @returns {Object} Updated list and listener flag
 */
export const removePropertyFromPropertyList = (
  list = [],
  screenName,
  propertyId,
  listenerList = [],
  listenerFlag = false
) => {
  if (!list || !Array.isArray(list)) {
    weLogs('ERROR: removePropertyFromPropertyList - invalid list');
    return { updatedList: [], listenerFlag: false };
  }

  if (!screenName || !propertyId) {
    weLogs('ERROR: removePropertyFromPropertyList - screenName and propertyId are required');
    return { updatedList: list, listenerFlag };
  }

  const updatedList = [...list];

  for (let index = updatedList.length - 1; index >= 0; index--) {
    const val = updatedList[index];
    if (val && val.screenName === screenName) {
      if (val.propertyList && Array.isArray(val.propertyList)) {
        val.propertyList = val.propertyList.filter(
          (property) => property && property.propertyId !== propertyId
        );

        weLogs('PropertyListUtils: updated list after removing property', updatedList);

        if (val.propertyList.length === 0) {
          updatedList.splice(index, 1);
        }
      }
    }
  }

  if (updatedList.length === 0 && listenerFlag) {
    listenerFlag = false;
    weLogs('PropertyListUtils: All listeners removed', listenerList);
  }

  return { updatedList, listenerFlag };
};

/**
 * Get property details from the list
 * @param {Array} list - Property list
 * @param {Object} weCampaignData - Campaign data
 * @returns {Object|null} Property details or null
 */
export const getPropertyDetails = (list = [], weCampaignData = {}) => {
  if (!list || !Array.isArray(list) || list.length === 0) {
    return null;
  }

  if (!weCampaignData || !weCampaignData.targetViewId) {
    return null;
  }

  const { targetViewId } = weCampaignData;
  const lastScreen = list[list.length - 1];

  if (lastScreen && lastScreen.propertyList && Array.isArray(lastScreen.propertyList)) {
    return lastScreen.propertyList.find(
      (val) => val && val.propertyId === targetViewId
    ) || null;
  }

  return null;
};

/**
 * Send onDataReceived event to registered callback
 * @param {Array} list - Property list
 * @param {Object} data - Event data
 */
export const sendOnDataReceivedEvent = (list = [], data = {}) => {
  if (!data) {
    weLogs('ERROR: sendOnDataReceivedEvent - data is required');
    return;
  }

  const { targetViewId = '', campaignId = '', payloadData = '{}' } = data;
  let payload = {};

  try {
    payload = JSON.parse(payloadData);
  } catch (parseError) {
    weLogs('ERROR: Failed to parse payloadData:', parseError);
  }

  const weCampaignData = {
    targetViewId,
    campaignId,
    payload,
  };

  const propertyItem = getPropertyDetails(list, weCampaignData);
  weLogs('PropertyListUtils: onDataReceived event', weCampaignData);

  if (propertyItem && propertyItem.callbacks && typeof propertyItem.callbacks.onDataReceived === 'function') {
    try {
      propertyItem.callbacks.onDataReceived(weCampaignData);
    } catch (callbackError) {
      weLogs('ERROR: onDataReceived callback failed:', callbackError);
    }
  }
};

/**
 * Send onRendered event to registered callback
 * @param {Array} list - Property list
 * @param {Object} data - Event data
 */
export const sendOnRenderedEvent = (list = [], data = {}) => {
  if (!data) {
    weLogs('ERROR: sendOnRenderedEvent - data is required');
    return;
  }

  const { targetViewId = '', campaignId = '', payloadData = '{}' } = data;
  let payload = {};

  try {
    payload = JSON.parse(payloadData);
  } catch (parseError) {
    weLogs('ERROR: Failed to parse payloadData:', parseError);
  }

  const weCampaignData = {
    targetViewId,
    campaignId,
    payload,
  };

  weLogs('PropertyListUtils: onRendered event', weCampaignData);

  const propertyItem = getPropertyDetails(list, weCampaignData);
  if (propertyItem && propertyItem.callbacks && typeof propertyItem.callbacks.onRendered === 'function') {
    try {
      propertyItem.callbacks.onRendered(weCampaignData);
    } catch (callbackError) {
      weLogs('ERROR: onRendered callback failed:', callbackError);
    }
  }
};

/**
 * Send onPlaceholderException event to registered callback
 * @param {Array} list - Property list
 * @param {Object} data - Event data
 */
export const sendOnExceptionEvent = (list = [], data = {}) => {
  if (!data) {
    weLogs('ERROR: sendOnExceptionEvent - data is required');
    return;
  }

  weLogs('PropertyListUtils: onPlaceholderException event', data);

  const { targetViewId = '' } = data;
  const weCampaignData = { targetViewId };

  const propertyItem = getPropertyDetails(list, weCampaignData);
  if (propertyItem && propertyItem.callbacks && typeof propertyItem.callbacks.onPlaceholderException === 'function') {
    try {
      propertyItem.callbacks.onPlaceholderException(data);
    } catch (callbackError) {
      weLogs('ERROR: onPlaceholderException callback failed:', callbackError);
    }
  }
};
