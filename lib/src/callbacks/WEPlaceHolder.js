import {weLogs} from '../utils/weLogs';
import
WEPersonalizationBridge,
{eventEmitter} from '../bridge/WEPersonalizationBridge';
import {
  registerPropertyList,
  removePropertyFromPropertyList,
  sendOnDataReceivedEvent,
  sendOnExceptionEvent,
} from '../utils/PropertyListUtils';
import {Platform} from 'react-native';

let customOnDataReceivedListener = null;
let customExceptionListener = null;
let isCustomListenerAdded = false;
let customPropertyList = [];

export const registerWEPlaceholderCallback = (
    androidPropertyId,
    iosPropertyId,
    screenName,
    onDataReceivedCb,
    onPlaceholderExceptionCb,
) => {
  const propertyId = Platform.OS === 'ios'? iosPropertyId : androidPropertyId;

  // Validate required parameters
  if (!propertyId || !screenName) {
    console.error('registerWEPlaceholderCallback: propertyId and screenName are required');
    return;
  }

  weLogs(
      'customPH: Registering for ',
      propertyId,
  );
  
  try {
    WEPersonalizationBridge.registerProperty(propertyId, screenName);
  } catch (error) {
    console.error('registerWEPlaceholderCallback: Error calling registerProperty', error);
    return;
  }
  customPropertyList = registerPropertyList(
      customPropertyList,
      screenName,
      propertyId,
      onDataReceivedCb,
      null,
      onPlaceholderExceptionCb,
  );
  weLogs(
      'customPH: registerWEPlaceholderCallback registered customPropertyList',
      customPropertyList,
  );
  if (!isCustomListenerAdded) {
    weLogs(
        'customPH: New Listeners Added',
        customPropertyList,
    );

    customOnDataReceivedListener = eventEmitter.addListener(
        'onCustomDataReceived',
        (data) => {
          weLogs('customPH: onCustomDataReceived list', data);

          sendOnDataReceivedEvent(customPropertyList, data);
        },
    );
    customExceptionListener = eventEmitter.addListener(
        'onCustomPlaceholderException',
        (data) => {
          weLogs('customPH: onCustomPlaceholderException list', data);
          sendOnExceptionEvent(customPropertyList, data);
        },
    );
    isCustomListenerAdded = true;
  } else {
    weLogs(
        'customPH: CustomListener is already Added',
    );
  }
};

export const deregisterWEPlaceholderCallback = (androidPropertyId, iosPropertyId, screen) => {
  const propertyId = Platform.OS === 'ios' ? iosPropertyId : androidPropertyId;
  
  // Validate required parameters
  if (!propertyId) {
    console.error('deregisterWEPlaceholderCallback: propertyId is required');
    return;
  }
  
  weLogs(
      'customPH: deregisterWEPlaceholderCallback! - Event Listener called ->',
  );

  try {
    WEPersonalizationBridge.deregisterProperty(propertyId);
  } catch (error) {
    console.error('deregisterWEPlaceholderCallback: Error calling deregisterProperty', error);
    return;
  }
  const listenerList = [customOnDataReceivedListener, customExceptionListener];
  const {updatedList, listenerFlag} = removePropertyFromPropertyList(
      customPropertyList,
      screen,
      propertyId,
      listenerList,
      isCustomListenerAdded,
  );
  isCustomListenerAdded = listenerFlag;
  customPropertyList = updatedList;
  if (!listenerFlag) {
    customOnDataReceivedListener.remove();
    customExceptionListener.remove();
  }
};

export const trackClick = (propertyId = '', map = null) => {
  weLogs('customPH: trackClick ');
  WEPersonalizationBridge.trackClick(propertyId, map);
};

export const trackImpression = (propertyId = '', map = null) => {
  weLogs('customPH: trackImpression ');
  WEPersonalizationBridge.trackImpression(propertyId, map);
};
