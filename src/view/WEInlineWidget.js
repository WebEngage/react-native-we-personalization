import React from 'react';
import { MyLogs } from '../utils/MyLogs';
import {
  eventEmitter,
  WebengagePersonalizationView,
} from '../bridge/WEPersonalizationBridge';
import {
  registerPropertyList,
  removePropertyFromPropertyList,
  sendOnDataReceivedEvent,
  sendOnExceptionEvent,
  sendOnRenderedEvent,
} from '../utils/PropertyListUtils';

let dataReceivedListener = null;
let renderListerner = null;
let exceptionalListener = null;
let isListenerAdded = false;
let propertyProcessor = [];

const WEInlineWidget = (props) => {
  const {
    propertyId = 0,
    screenName = '',
    onRendered = null,
    onDataReceived = null,
    onPlaceholderException = null,
  } = props;


  React.useEffect(() => {
    MyLogs('WEInlineWidget: Attached/Mounted ', propertyId);
    return () => {
      MyLogs('WEInlineWidget: Destroyed/UnMounted ', propertyId);
      const listenersList = [
        dataReceivedListener,
        renderListerner,
        exceptionalListener,
      ];
      const { updatedList, listenerFlag } = removePropertyFromPropertyList(
        propertyProcessor,
        screenName,
        propertyId,
        listenersList,
        isListenerAdded
      );
      if(!listenerFlag) {
        dataReceivedListener?.remove()
        renderListerner?.remove()
        exceptionalListener?.remove()
      }
      isListenerAdded = listenerFlag;
      propertyProcessor = updatedList;
    };
  },[]);

  const propList = registerPropertyList(
    propertyProcessor,
    screenName,
    propertyId,
    onDataReceived,
    onRendered,
    onPlaceholderException
  );
  propertyProcessor = [...propList];
  MyLogs("WEInlineWidget: propertyProcessor",propertyProcessor)
  if (!isListenerAdded) {
    dataReceivedListener = eventEmitter.addListener(
      'onDataReceived',
      (data) => {
        sendOnDataReceivedEvent(propertyProcessor, data);
      }
    );

    renderListerner = eventEmitter.addListener('onRendered', (data) => {
      sendOnRenderedEvent(propertyProcessor, data);
    });

    exceptionalListener = eventEmitter.addListener(
      'onPlaceholderException',
      (data) => {
        sendOnExceptionEvent(propertyProcessor, data);
      }
    );
    isListenerAdded = true;
  }

  return <WebengagePersonalizationView {...props} />;
};

export default React.memo(WEInlineWidget);
