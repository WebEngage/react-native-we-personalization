import { MyLogs } from '../utils/MyLogs';
import WEPersonalizationBridge, { eventEmitter } from '../bridge/WEPersonalizationBridge';
import {
  registerPropertyList,
  removePropertyFromPropertyList,
  sendOnDataReceivedEvent,
  sendOnExceptionEvent,
} from '../utils/PropertyListUtils';

let customOnDataReceivedListener = null;
let customExceptionListener = null;
let isCustomListenerAdded = false;
let customPropertyList = [];

export const registerWEPlaceholderCallback = (
  propertyId,
  screenName,
  onDataReceivedCb,
  onPlaceholderExceptionCb
) => {
  MyLogs(
    'customPH: Registering for ',
    propertyId
  );
  WEPersonalizationBridge.registerCallback(propertyId, screenName);
  customPropertyList = registerPropertyList(
    customPropertyList,
    screenName,
    propertyId,
    onDataReceivedCb,
    null,
    onPlaceholderExceptionCb
  );
  MyLogs(
    'customPH: registerWEPlaceholderCallback registered customPropertyList',
    customPropertyList
  );
  if (!isCustomListenerAdded) {
    MyLogs(
      'customPH: New Listeners Added',
      customPropertyList
    );

    customOnDataReceivedListener = eventEmitter.addListener(
      'onCustomDataReceived',
      (data) => {
        MyLogs('customPH: onCustomDataReceived list', data);

        sendOnDataReceivedEvent(customPropertyList, data);
      }
    );
    customExceptionListener = eventEmitter.addListener(
      'onCustomPlaceholderException',
      (data) => {
        MyLogs('customPH: onCustomPlaceholderException list', data);
        sendOnExceptionEvent(customPropertyList, data);
      }
    );
    isCustomListenerAdded = true;
  } else {
    MyLogs(
      'customPH: CustomListener is already Added'
    );
  }
};

export const deregisterWEPlaceholderCallback = (propertyId, screen) => {
  MyLogs(
    'customPH: deregisterWEPlaceholderCallback! - Event Listener called ->'
  );

  WEPersonalizationBridge.unRegisterCallback(propertyId);
  const listenersList = [customOnDataReceivedListener, customExceptionListener];
  const { updatedList, listenerFlag } = removePropertyFromPropertyList(
    customPropertyList,
    screen,
    propertyId,
    listenersList,
    isCustomListenerAdded
  );
  isCustomListenerAdded = listenerFlag;
  customPropertyList = updatedList;
  if(!listenerFlag) {
    customOnDataReceivedListener.remove();
    customExceptionListener.remove();
  }
};

export const trackClick = (propertyId = '', map = null) => {
  MyLogs('customPH: trackClick ');
  WEPersonalizationBridge.trackClick(propertyId, map)
}

export const trackImpression = (propertyId = '', map = null) => {
  MyLogs('customPH: trackImpression ');
  WEPersonalizationBridge.trackImpression(propertyId, map)
}
