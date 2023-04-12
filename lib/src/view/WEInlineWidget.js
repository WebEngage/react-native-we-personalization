import React from 'react';
import PropTypes from 'prop-types';
import {Platform} from 'react-native';

import {weLogs} from '../utils/weLogs';
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
let renderListener = null;
let exceptionalListener = null;
let isListenerAdded = false;
let propertyProcessor = [];

const WEInlineWidget = (props) => {
  const {
    androidPropertyId = '',
    iosPropertyId = 0,
    screenName = '',
    onRendered = null,
    onDataReceived = null,
    onPlaceholderException = null,
    style = {},
  } = props;
  const propertyId = Platform.OS === 'ios'? iosPropertyId : androidPropertyId;
  const updatedProps = {
    propertyId,
    screenName,
    onDataReceived,
    onRendered,
    onPlaceholderException,
    style,
  };


  React.useEffect(() => {
    weLogs('WEInlineWidget: Attached/Mounted ', propertyId);
    return () => {
      weLogs('WEInlineWidget: Destroyed/UnMounted ', propertyId);
      const listenerList = [
        dataReceivedListener,
        renderListener,
        exceptionalListener,
      ];
      const {updatedList, listenerFlag} = removePropertyFromPropertyList(
          propertyProcessor,
          screenName,
          propertyId,
          listenerList,
          isListenerAdded,
      );
      if (!listenerFlag) {
        dataReceivedListener?.remove();
        renderListener?.remove();
        exceptionalListener?.remove();
      }
      isListenerAdded = listenerFlag;
      propertyProcessor = updatedList;
    };
  }, []);

  const propList = registerPropertyList(
      propertyProcessor,
      screenName,
      propertyId,
      onDataReceived,
      onRendered,
      onPlaceholderException,
  );
  propertyProcessor = [...propList];
  weLogs('WEInlineWidget: propertyProcessor', propertyProcessor);
  if (!isListenerAdded) {
    dataReceivedListener = eventEmitter.addListener(
        'onDataReceived',
        (data) => {
          sendOnDataReceivedEvent(propertyProcessor, data);
        },
    );

    renderListener = eventEmitter.addListener('onRendered', (data) => {
      sendOnRenderedEvent(propertyProcessor, data);
    });

    exceptionalListener = eventEmitter.addListener(
        'onPlaceholderException',
        (data) => {
          sendOnExceptionEvent(propertyProcessor, data);
        },
    );
    isListenerAdded = true;
  }

  return <WebengagePersonalizationView {...updatedProps} />;
};

WEInlineWidget.propTypes = {
  androidPropertyId: PropTypes.string,
  iosPropertyId: PropTypes.number,
  screenName: PropTypes.string.isRequired, // Add the missing prop here
  onRendered: PropTypes.func,
  onDataReceived: PropTypes.func,
  onPlaceholderException: PropTypes.func,
  style: PropTypes.object,
};

export default React.memo(WEInlineWidget);
