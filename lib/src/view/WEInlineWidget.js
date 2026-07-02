import React from 'react';
import PropTypes from 'prop-types';
import { Platform } from 'react-native';

import { weLogs } from '../utils/weLogs';
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

const listeners = {
  dataReceived: null,
  render: null,
  exception: null,
};
let isListenerAdded = false;
let propertyProcessor = [];

/**
 * WEInlineWidget - WebEngage Personalization Inline Widget Component
 * 
 * This component supports both New Architecture (Fabric) and Legacy Architecture
 * It automatically detects the runtime and uses appropriate native implementation
 * 
 * Features:
 * - Cross-platform support (iOS/Android)
 * - Architecture-agnostic (Fabric/Legacy)
 * - Event handling for data received, rendered, and exceptions
 * - Automatic property registration and cleanup
 */
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

  if (!screenName) {
    console.error('[WE-Inline-JS] screenName is required');
    return null;
  }

  if (!eventEmitter) {
    console.error('[WE-Inline-JS] eventEmitter not available');
    return null;
  }

  const propertyId = Platform.OS === 'ios' ? String(iosPropertyId) : androidPropertyId;

  const updatedProps = {
    propertyId,
    screenName,
    onDataReceived,
    onRendered,
    onPlaceholderException,
    style,
  };

  React.useEffect(() => {
    const architecture = global.__turboModuleProxy ? 'Fabric' : 'Legacy';
    weLogs(`Running on ${architecture}`);
  }, []);


  React.useEffect(() => {
    weLogs(`mount: property=${propertyId}, screen=${screenName}`);
    return () => {
      weLogs(`unmount: property=${propertyId}, screen=${screenName}`);

      const listenerList = [
        listeners.dataReceived,
        listeners.render,
        listeners.exception,
      ];

      const { updatedList, listenerFlag } = removePropertyFromPropertyList(
        propertyProcessor,
        screenName,
        propertyId,
        listenerList,
        isListenerAdded
      );

      if (!listenerFlag) {
        Object.values(listeners).forEach((listener) => {
          try {
            listener?.remove();
          } catch (error) {
            console.error('[WE-Inline-JS] Failed to remove listener:', error);
          }
        });
        listeners.dataReceived = null;
        listeners.render = null;
        listeners.exception = null;
      }

      isListenerAdded = listenerFlag;
      propertyProcessor = updatedList;
    };
  }, [propertyId, screenName]);

  const propList = registerPropertyList(
    propertyProcessor,
    screenName,
    propertyId,
    onDataReceived,
    onRendered,
    onPlaceholderException
  );
  propertyProcessor = [...propList];
  weLogs(`propertyProcessor: screens=${propertyProcessor.length}`);

  if (!isListenerAdded && eventEmitter) {
    try {
      listeners.dataReceived = eventEmitter.addListener('onDataReceived', (data) => {
        try {
          sendOnDataReceivedEvent(propertyProcessor, data);
        } catch (error) {
          console.error('[WE-Inline-JS] onDataReceived handler failed:', error);
        }
      });

      listeners.render = eventEmitter.addListener('onRendered', (data) => {
        try {
          sendOnRenderedEvent(propertyProcessor, data);
        } catch (error) {
          console.error('[WE-Inline-JS] onRendered handler failed:', error);
        }
      });

      listeners.exception = eventEmitter.addListener('onPlaceholderException', (data) => {
        try {
          sendOnExceptionEvent(propertyProcessor, data);
        } catch (error) {
          console.error('[WE-Inline-JS] onPlaceholderException handler failed:', error);
        }
      });

      isListenerAdded = true;
    } catch (error) {
      console.error('[WE-Inline-JS] Failed to add event listeners:', error);
    }
  }

  return <WebengagePersonalizationView {...updatedProps} />;
};

WEInlineWidget.propTypes = {
  androidPropertyId: PropTypes.string,
  iosPropertyId: PropTypes.number,
  screenName: PropTypes.string.isRequired,
  onRendered: PropTypes.func,
  onDataReceived: PropTypes.func,
  onPlaceholderException: PropTypes.func,
  style: PropTypes.object,
};

export default React.memo(WEInlineWidget);
