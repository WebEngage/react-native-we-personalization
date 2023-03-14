import React from 'react';
import { MyLogs } from './MyLogs';
import {
  eventEmitter,
  WebengagePersonalizationView,
} from './PersonalizationBridge';
import {
  registerPropertyList,
  removePropertyFromPropertyList,
  sendOnDataReceivedEvent,
  sendOnExceptionEvent,
  sendOnRenderedEvent,
} from './PropertyListUtils';

let dataReceivedListener = null;
let renderListerner = null;
let exceptionalListener = null;
let isListenerAdded = false;
let propertyProcessor = [];

const WEInlineView = (props) => {
  const {
    propertyId = 0,
    screenName = '',
    onRendered = null,
    onDataReceived = null,
    onPlaceholderException = null,
  } = props;


  React.useEffect(() => {
    MyLogs('WEInlineView: Attached/Mounted ', propertyId);

    return () => {
      MyLogs('WEInlineView: Destroyed/UnMounted ', propertyId);

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
  MyLogs("WEInlineView: propertyProcessor",propertyProcessor)
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

// export default WEInlineView;
export default React.memo(WEInlineView);
