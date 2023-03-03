import React from 'react';
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
    console.log('$$$$ WEInlineView: Attached/Mounted ', propertyId);

    return () => {
      console.log('$$$$ WEInlineView: Destroyed/UnMounted ', propertyId);

      const listenersList = [
        dataReceivedListener,
        renderListerner,
        exceptionalListener,
      ];
  console.log("@@@ List before removing",propertyProcessor)

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
  console.log("@@@ registerPropertyList",propertyProcessor)
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
