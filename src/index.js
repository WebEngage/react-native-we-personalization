import {
  requireNativeComponent,
  UIManager,
  Platform,
  NativeModules,
  Text,
  View,
  NativeEventEmitter,
} from 'react-native';
import React from 'react';

const LINKING_ERROR =
  `The package 'react-native-webengage-personalization' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const BRIDGE_ERROR =
  `bridge package 'react-native-webengage-personalization' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const ComponentName = 'WebengagePersonalizationView';
const eventEmitter = new NativeEventEmitter(
  NativeModules.PersonalizationBridge
);
let isListenerAdded = false;
const propertyProcessor = {
  screenName: '',
  propertyList: [],
};

const PersonalizationBridge = NativeModules.PersonalizationBridge
  ? NativeModules.PersonalizationBridge
  : new Proxy(
      {},
      {
        get() {
          throw new Error(BRIDGE_ERROR);
        },
      }
    );

export const WEPersonalization = (props) => {
  console.log('props in webengage docs - ', props);

  // TODO - Remove All Listeners when screen is changed - also delete propertyList and create new
  // eventEmitter.removeAllListeners();

  const { propertyId, personalizationCallback } = props;

  // pushing properties inside an array of listeners

  if (
    !propertyProcessor?.propertyList
      .flatMap(Object.keys)
      .includes(props.propertyId)
  ) {
    let obj = {};
    obj[propertyId] = personalizationCallback;
    propertyProcessor.propertyList.push(obj);
    propertyProcessor.screenName = props.screenName;
  }

  console.log('propertyProcessors ---- ', propertyProcessor);
  if (!isListenerAdded) {
    // TODO - Error is coming bcz currently value is static and hence it is overriding
    eventEmitter.addListener('onDataReceived', (event) => {
      console.log('onDataReceived - Event Listerner called ->', event); // "someValue"
      if (
        Object.keys(propertyProcessor.propertyList).includes(event.targetViewId)
      ) {
        //  Trigger the callback registered for the propertyList
      }
    });
    isListenerAdded = true;
  }
  return <WebengagePersonalizationView {...props} />;
};

export const WebengagePersonalizationView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };

export function multiply(a, b) {
  return PersonalizationBridge.multiply(a, b);
}

// Below code is example for multiple call
export function add(a, b) {
  return PersonalizationBridge.add(a, b);
}
