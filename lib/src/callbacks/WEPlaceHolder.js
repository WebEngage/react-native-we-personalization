import { weLogs } from '../utils/weLogs';
import WEPersonalizationBridge, { eventEmitter } from '../bridge/WEPersonalizationBridge';
import {
  registerPropertyList,
  removePropertyFromPropertyList,
  sendOnDataReceivedEvent,
  sendOnExceptionEvent,
} from '../utils/PropertyListUtils';
import { Platform } from 'react-native';

let isCustomListenerAdded = false;
let customPropertyList = [];
const listeners = {
  customOnDataReceived: null,
  customException: null,
};

/**
 * Register placeholder callbacks
 * @param {string} androidPropertyId - Android property ID
 * @param {number} iosPropertyId - iOS property ID
 * @param {string} screenName - Screen name
 * @param {Function} onDataReceivedCb - Data received callback
 * @param {Function} onPlaceholderExceptionCb - Exception callback
 */
export const registerWEPlaceholderCallback = (
  androidPropertyId,
  iosPropertyId,
  screenName,
  onDataReceivedCb,
  onPlaceholderExceptionCb
) => {
  if (!androidPropertyId && !iosPropertyId) {
    weLogs('ERROR: registerWEPlaceholderCallback - propertyId is required');
    return;
  }

  if (!screenName) {
    weLogs('ERROR: registerWEPlaceholderCallback - screenName is required');
    return;
  }

  if (!eventEmitter) {
    weLogs('ERROR: Event emitter not available');
    return;
  }

  const propertyId = Platform.OS === 'ios' ? String(iosPropertyId) : androidPropertyId;

  weLogs('customPH: Registering for', propertyId);

  try {
    WEPersonalizationBridge?.registerProperty(propertyId, screenName);
  } catch (error) {
    weLogs('ERROR: registerProperty failed:', error);
    return;
  }

  try {
    customPropertyList = registerPropertyList(
      customPropertyList,
      screenName,
      propertyId,
      onDataReceivedCb,
      null,
      onPlaceholderExceptionCb
    );

    weLogs('customPH: registered customPropertyList', customPropertyList);

    if (!isCustomListenerAdded) {
      weLogs('customPH: Adding new listeners');

      listeners.customOnDataReceived = eventEmitter.addListener(
        'onCustomDataReceived',
        (data) => {
          try {
            weLogs('customPH: onCustomDataReceived', data);
            sendOnDataReceivedEvent(customPropertyList, data);
          } catch (error) {
            weLogs('ERROR: onCustomDataReceived callback error:', error);
          }
        }
      );

      listeners.customException = eventEmitter.addListener(
        'onCustomPlaceholderException',
        (data) => {
          try {
            weLogs('customPH: onCustomPlaceholderException', data);
            sendOnExceptionEvent(customPropertyList, data);
          } catch (error) {
            weLogs('ERROR: onCustomPlaceholderException callback error:', error);
          }
        }
      );

      isCustomListenerAdded = true;
    } else {
      weLogs('customPH: Listeners already added');
    }
  } catch (error) {
    weLogs('ERROR: registerWEPlaceholderCallback failed:', error);
  }
};

/**
 * Deregister placeholder callbacks
 * @param {string} androidPropertyId - Android property ID
 * @param {number} iosPropertyId - iOS property ID
 * @param {string} screen - Screen name
 */
export const deregisterWEPlaceholderCallback = (androidPropertyId, iosPropertyId, screen) => {
  if (!androidPropertyId && !iosPropertyId) {
    weLogs('ERROR: deregisterWEPlaceholderCallback - propertyId is required');
    return;
  }

  const propertyId = Platform.OS === 'ios' ? String(iosPropertyId) : androidPropertyId;

  weLogs('customPH: deregisterWEPlaceholderCallback called');

  try {
    WEPersonalizationBridge?.deregisterProperty(propertyId);
  } catch (error) {
    weLogs('ERROR: deregisterProperty failed:', error);
  }

  try {
    const listenerList = [listeners.customOnDataReceived, listeners.customException];
    const { updatedList, listenerFlag } = removePropertyFromPropertyList(
      customPropertyList,
      screen,
      propertyId,
      listenerList,
      isCustomListenerAdded
    );

    isCustomListenerAdded = listenerFlag;
    customPropertyList = updatedList;

    if (!listenerFlag) {
      Object.values(listeners).forEach((listener) => {
        try {
          listener?.remove();
        } catch (error) {
          weLogs('ERROR: Failed to remove listener:', error);
        }
      });

      listeners.customOnDataReceived = null;
      listeners.customException = null;
    }
  } catch (error) {
    weLogs('ERROR: deregisterWEPlaceholderCallback failed:', error);
  }
};

/**
 * Track click event
 * @param {string|number} propertyId - Property ID (accepts both string and number)
 * @param {Object|null} map - Additional attributes
 */
export const trackClick = (propertyId = '', map = null) => {
  if (!propertyId) {
    weLogs('WARN: trackClick - propertyId is empty');
  }

  try {
    weLogs('customPH: trackClick', propertyId);
    WEPersonalizationBridge?.trackClick(String(propertyId), map || {});
  } catch (error) {
    weLogs('ERROR: trackClick failed:', error);
  }
};

/**
 * Track impression event
 * @param {string|number} propertyId - Property ID (accepts both string and number)
 * @param {Object|null} map - Additional attributes
 */
export const trackImpression = (propertyId = '', map = null) => {
  if (!propertyId) {
    weLogs('WARN: trackImpression - propertyId is empty');
  }

  try {
    weLogs('customPH: trackImpression', propertyId);
    WEPersonalizationBridge?.trackImpression(String(propertyId), map || {});
  } catch (error) {
    weLogs('ERROR: trackImpression failed:', error);
  }
};
