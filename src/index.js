import {
  requireNativeComponent,
  UIManager,
  Platform,
  NativeEventEmitter,
} from 'react-native';
import React from 'react';

const LINKING_ERROR =
  `The package 'react-native-webengage-personalization' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const ComponentName = 'WebengagePersonalizationView';
const eventEmitter = new NativeEventEmitter();
let listener = null;
let isListenerAdded = false;
const propertyProcessor = {
  screenName: '',
  propertyList: [],
};

export const WEPersonalization = (props) => {
  console.log('props in webengage docs - ', props);
  const { propertyId = '', screenName = '', personalizationCallback } = props;

  React.useEffect(() => {
    return () => {
      console.log("Unmounting Component")
      if (listener) {
        isListenerAdded = false;
        listener.remove();
      }
    };
  });

  const propertyListArr = propertyProcessor?.propertyList.flatMap(
    (curr) => curr.propertyId
  );
  if (propertyProcessor.screenName !== screenName) {
    propertyProcessor.screenName = screenName;
    propertyProcessor.propertyList = [];
    propertyListArr?.splice(0);
  }

  if (!propertyListArr.includes(propertyId)) {
    let obj = {};
    obj.propertyId = propertyId;
    obj.callback = personalizationCallback;
    propertyProcessor.propertyList.push(obj);
  }

  console.log('## propertyProcessors ---- ', propertyProcessor);
  if (!isListenerAdded) {
    listener = eventEmitter.addListener('onDataReceived', (event) => {
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
};

export const WebengagePersonalizationView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
