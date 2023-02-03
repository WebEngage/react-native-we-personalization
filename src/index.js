import {
  requireNativeComponent,
  UIManager,
  Platform,
  NativeEventEmitter,
  NativeModules,
} from 'react-native';
import React from 'react';

const LINKING_ERROR =
  `The package 'react-native-webengage-personalization' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const ComponentName = 'WebengagePersonalizationView';
const eventEmitter = new NativeEventEmitter(
  NativeModules.PersonalizationBridge
);
let dataReceivedListener = null;
let renderListerner = null;
let exceptionalListener = null;
let isListenerAdded = false;
// const propertyProcessor = [
//   {
//     screenName: '',
//     propertyList: [],
//   },
// ];
const propertyProcessor = [];

export const WEPersonalization = (props) => {
  console.log('props in webengage docs - ', props);
  const {
    propertyId = 0,
    screenName = '',
    onRendered = null,
    onDataReceived = null,
    onPlaceholderException = null,
  } = props;
  var myRef = React.createRef();

  React.useEffect(() => {
    // NativeModules.WebengagePersonalizationView.myMethod("tag sent form RN")
    // NativeModules.WebengagePersonalizationView.myMethod("tag sent form RN");

    // UIManager.dispatchViewManagerCommand(
    //   findNodeHandle(myRef),
    //   // we are calling the 'create' command
    //   UIManager.WebengagePersonalizationView.Commands.myMethod,
    //   null
    // );

    // UIManager.MyViewManager.Commands.create.toString(),
    // Commands.create.toString()
    // UIManager.dispatchViewManagerCommand(
    //   "viewId",
    //   // we are calling the 'create' command
    //   UIManager.MyViewManager.Commands.create.toString(),
    //   [viewId],
    // );
    console.log('propertyProcessor latest -> ', propertyProcessor);

    return () => {
      console.log(
        'Unmounting property- ',
        propertyId,
        ' from screen-',
        screenName
      );
      if (dataReceivedListener) {
        isListenerAdded = false;
        dataReceivedListener?.remove();
        renderListerner?.remove();
        exceptionalListener?.remove();
        console.log(
          ' ################# removing Listeners - onRendered - ' + propertyId
        );
      }
      removeScreenFromPropertyList();
      console.log('propertyProcessor after slicing -> ', propertyProcessor);
    };
  });

  const screenIndex = getLatestScreenIndex();
  if (screenIndex === -1) {
    propertyProcessor.push({
      screenName: screenName,
      propertyList: [],
    });
  }

  function getLatestScreenIndex() {
    return propertyProcessor.findIndex((obj) => obj.screenName === screenName);
  }

  const removeScreenFromPropertyList = () => {
    propertyProcessor?.map((val, index) => {
      if (val.screenName === screenName) {
        propertyProcessor.splice(index, 1);
      }
    });
  };

  const latestScreenIndex = getLatestScreenIndex();
  if (
    !propertyProcessor[latestScreenIndex].propertyList
      .flatMap((curr) => curr.screenName)
      .includes(propertyId)
  ) {
    let obj = {};
    obj.propertyId = propertyId;
    obj.callbacks = {
      onRendered: onRendered,
      onPlaceholderException: onPlaceholderException,
      onDataReceived: onDataReceived,
    };
    if (latestScreenIndex > -1) {
      propertyProcessor[latestScreenIndex].propertyList.push(obj);
    }
  }

  if (!isListenerAdded) {
    // onDataReceived
    dataReceivedListener = eventEmitter.addListener(
      'onDataReceived',
      (event) => {
        console.log('onDataReceived - Event Listerner called ->', event);
        if (propertyProcessor?.length) {
          propertyProcessor[latestScreenIndex]?.propertyList?.map((val) => {
            if (val.propertyId === event.targetViewId) {
              val?.callbacks?.onDataReceived &&
                val?.callbacks?.onDataReceived(event);
            }
          });
        }
      }
    );

    // onRendered
    renderListerner = eventEmitter.addListener('onRendered', (event) => {
      console.log('onRendered - Event Listerner called ->', event);
      if (propertyProcessor?.length) {
        propertyProcessor[latestScreenIndex]?.propertyList?.map((val) => {
          if (val.propertyId === event.targetViewId) {
            val?.callbacks?.onRendered && val?.callbacks?.onRendered(event);
          }
        });
      }
    });

    //  = eventEmitter.addListener('testAk', (event) => {
    //   console.log('testAk - propertyProcessor data  ->', propertyProcessor);
    //   if (propertyProcessor?.length) {
    //     propertyProcessor[latestScreenIndex]?.propertyList?.map((val) => {
    //       if (val.propertyId === event.targetViewId) {
    //         val?.callbacks?.onRendered && val?.callbacks?.onRendered(event);
    //       }
    //     });
    //   }
    // });

    // eventEmitter.addListener('onPropertyCacheCleared', (event) => {
    //   console.log('onPropertyCacheCleared - Event Listerner called ->', event);
    //   propertyProcessor[latestScreenIndex].propertyList?.map((val) => {
    //     if (val.screenName === event.screenName) {
    //       NativeModules.WebengagePersonalizationView.myMethod("tag sent form RN")
    //       //  return propertyIds
    //     }
    //   });
    // });

    // onPlaceholderException
    exceptionalListener = eventEmitter.addListener(
      'onPlaceholderException',
      (event) => {
        console.log(
          'onPlaceholderException - Event Listerner called ->',
          event
        );
        if (propertyProcessor?.length) {
          propertyProcessor[latestScreenIndex]?.propertyList?.map((val) => {
            if (val.propertyId === event.targetViewId) {
              val?.callbacks?.onPlaceholderException &&
                val?.callbacks?.onPlaceholderException(event);
            }
          });
        }
      }
    );

    isListenerAdded = true;
  }

  // return <WebengagePersonalizationView ref={viewRef => myRef = viewRef} {...props} />;
  return <WebengagePersonalizationView {...props} />;
};

export const WebengagePersonalizationView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
