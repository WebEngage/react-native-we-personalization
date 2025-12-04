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
  
  // Platform-specific property ID selection and conversion
  const propertyId = Platform.OS === 'ios' 
    ? String(iosPropertyId) 
    : androidPropertyId;
  
  // Props optimized for both Fabric and Legacy architectures
  const updatedProps = {
    propertyId,
    screenName,
    onDataReceived,
    onRendered,
    onPlaceholderException,
    style,
  };
  
  // Log architecture detection for debugging
  React.useEffect(() => {
    const architecture = global.__turboModuleProxy ? 'New Architecture (Fabric)' : 'Legacy Architecture';
    weLogs(`WEInlineWidget: Running on ${architecture}`);
  }, []);


  // Component lifecycle management with architecture-aware logging
  React.useEffect(() => {
    weLogs('WEInlineWidget: Attached/Mounted ', propertyId);
    return () => {
      weLogs('WEInlineWidget: Destroyed/UnMounted ', propertyId);
      
      // Clean up event listeners for both architectures
      const listenerList = [
        dataReceivedListener,
        renderListener,
        exceptionalListener,
      ];
      
      // Remove property from registry and manage listeners
      const {updatedList, listenerFlag} = removePropertyFromPropertyList(
          propertyProcessor,
          screenName,
          propertyId,
          listenerList,
          isListenerAdded,
      );
      
      // Clean up listeners if no more properties are registered
      if (!listenerFlag) {
        dataReceivedListener?.remove();
        renderListener?.remove();
        exceptionalListener?.remove();
      }
      
      isListenerAdded = listenerFlag;
      propertyProcessor = updatedList;
    };
  }, []);

  // Register property in the global property list for event handling
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
  
  // Set up event listeners (works with both TurboModules and Legacy modules)
  if (!isListenerAdded) {
    // Data received event - triggered when personalization content is fetched
    dataReceivedListener = eventEmitter.addListener(
        'onDataReceived',
        (data) => {
          sendOnDataReceivedEvent(propertyProcessor, data);
        },
    );

    // Render event - triggered when content is successfully rendered
    renderListener = eventEmitter.addListener('onRendered', (data) => {
      sendOnRenderedEvent(propertyProcessor, data);
    });

    // Exception event - triggered when there's an error in content loading/rendering
    exceptionalListener = eventEmitter.addListener(
        'onPlaceholderException',
        (data) => {
          sendOnExceptionEvent(propertyProcessor, data);
        },
    );
    isListenerAdded = true;
  }

  // Return the native view component (Fabric or Legacy based on architecture)
  return <WebengagePersonalizationView {...updatedProps} />;
};

/**
 * PropTypes definition for WEInlineWidget
 * Ensures type safety across both architectures
 */
WEInlineWidget.propTypes = {
  androidPropertyId: PropTypes.string,     // Android-specific property ID
  iosPropertyId: PropTypes.number,         // iOS-specific property ID  
  screenName: PropTypes.string.isRequired, // Screen identifier for analytics
  onRendered: PropTypes.func,              // Callback when content is rendered
  onDataReceived: PropTypes.func,          // Callback when data is received
  onPlaceholderException: PropTypes.func,  // Callback for error handling
  style: PropTypes.object,                 // Style object for the component
};

/**
 * Export memoized component for performance optimization
 * Works seamlessly with both Fabric and Legacy architectures
 */
export default React.memo(WEInlineWidget);
