import { MyLogs } from './MyLogs';
import PersonalizationBridge, { eventEmitter } from './PersonalizationBridge';
import {
  registerPropertyList,
  removePropertyFromPropertyList,
  sendOnDataReceivedEvent,
  sendOnExceptionEvent,
} from './PropertyListUtils';

let customOnDataReceivedListener = null;
let customExceptionListener = null;
let isCustomListenerAdded = false;
let customPropertyList = [];

export const registerCustomPlaceHolder = (
  propertyId,
  screenName,
  onDataReceivedCb,
  onPlaceholderExceptionCb
) => {
  MyLogs(
    'customPH: Registering for ',
    propertyId
  );
  PersonalizationBridge.registerCallback(propertyId, screenName);
  customPropertyList = registerPropertyList(
    customPropertyList,
    screenName,
    propertyId,
    onDataReceivedCb,
    null,
    onPlaceholderExceptionCb
  );
  MyLogs(
    'customPH: registerCustomPlaceHolder registered customPropertyList',
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

export const unRegisterCustomPlaceHolder = (propertyId, screen) => {
  MyLogs(
    'customPH: unRegisterCustomPlaceHolder! - Event Listener called ->'
  );

  PersonalizationBridge.unRegisterCallback(propertyId);
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

export const trackCustomClick = (propertyId = '', map = null) => {
  MyLogs('customPH: trackCustomClick ');
  PersonalizationBridge.trackClick(propertyId, map)
}

export const trackCustomImpression = (propertyId = '', map = null) => {
  MyLogs('customPH: trackImpression ');
  PersonalizationBridge.trackImpression(propertyId, map)
}
