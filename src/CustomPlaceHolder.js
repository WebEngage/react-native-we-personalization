import { MyLogs } from './MyLogs';
import PersonalizationBridge, { eventEmitter } from './PersonalizationBridge';
import {
  registerPropertyList,
  removePropertyFromPropertyList,
  sendOnDataReceivedEvent,
  sendOnExceptionEvent,
} from './PropertyListUtils';

let customOnRenderedListener = null;
let customExceptionListener = null;
let isCustomListenerAdded = false;
let customPropertyList = [];

export const registerCustomPlaceHolder = (
  propertyId,
  screenName,
  onDataReceivedCb,
  onPlaceholderExceptionCb
) => {
  PersonalizationBridge.registerCallback(propertyId);
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
    customOnRenderedListener = eventEmitter.addListener(
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
  }
};

export const unRegisterCustomPlaceHolder = (propertyId, screen) => {
  MyLogs(
    'customPH: unRegisterCustomPlaceHolder! - Event Listener called ->'
  );

  PersonalizationBridge.unRegisterCallback(propertyId);
  const listenersList = [customOnRenderedListener, customExceptionListener];
  const { updatedList, listenerFlag } = removePropertyFromPropertyList(
    customPropertyList,
    screen,
    propertyId,
    listenersList,
    isCustomListenerAdded
  );
  isCustomListenerAdded = listenerFlag;
  customPropertyList = updatedList;
};

export const trackCustomClick = () => {
  MyLogs('customPH: trackCustomClick ');
  PersonalizationBridge.trackClick()
}

export const trackCustomImpression = () => {
  MyLogs('customPH: trackImpression ');
  PersonalizationBridge.trackImpression()
}
