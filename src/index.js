import {
  requireNativeComponent,
  UIManager,
  Platform,
  NativeModules,
  Text,
  View,
  NativeEventEmitter,
  Dimensions,
  Animated,
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

export const WEPersonalization = React.forwardRef((props, ref) => {
  console.log('props in webengage docs - ', props);
  const { propertyId, personalizationCallback } = props;

  const propertyListArr = propertyProcessor?.propertyList.flatMap(
    (curr) => curr.propertyId
  );
  console.log('## ', propertyListArr);
  if (!propertyListArr.includes(props.propertyId)) {
    let obj = {};
    obj.propertyId = propertyId;
    obj.callback = personalizationCallback;
    propertyProcessor.propertyList.push(obj);
    propertyProcessor.screenName = props.screenName;
  }

  console.log('propertyProcessors ---- ', propertyProcessor);
  if (!isListenerAdded) {
    eventEmitter.addListener('onDataReceived', (event) => {
      console.log('onDataReceived - Event Listerner called ->', event);

      propertyProcessor.propertyList.map((val) => {
        if (val.propertyId === event.targetViewId) {
          console.log(' key found --- ', val);
          val?.callback(event);
        }
      });
    });
    isListenerAdded = true;
  }

  return <WebengagePersonalizationView {...props} />;
});

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
