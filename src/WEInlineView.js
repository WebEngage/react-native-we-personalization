import React from 'react';
import {
  eventEmitter,
  WebengagePersonalizationView,
} from './PersonalizationBridge';
import {
  registerPropertyList,
  removeScreenFromPropertyList,
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
    return () => {
      const listenersList = [
        dataReceivedListener,
        renderListerner,
        exceptionalListener,
      ];
      const { updatedList, listenerFlag } = removeScreenFromPropertyList(
        propertyProcessor,
        screenName,
        listenersList,
        isListenerAdded
      );
      isListenerAdded = listenerFlag;
      propertyProcessor = updatedList;
    };
  });

  propertyProcessor = registerPropertyList(
    propertyProcessor,
    screenName,
    propertyId,
    onDataReceived,
    onRendered,
    onPlaceholderException
  );

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

export default React.memo(WEInlineView);
