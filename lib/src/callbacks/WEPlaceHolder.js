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

  weLogs(
      'customPH: Registering for ',
      propertyId,
  );
  WEPersonalizationBridge.registerProperty(propertyId, screenName);
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
  weLogs(
      'customPH: deregisterWEPlaceholderCallback! - Event Listener called ->',
  );

  WEPersonalizationBridge.deregisterProperty(propertyId);
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
