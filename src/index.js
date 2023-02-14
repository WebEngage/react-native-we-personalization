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

  // TODO - user Init ();
  // register campaignCallback -> flutter_text

  var myRef = React.createRef();

  React.useEffect(() => {
    console.log('propertyProcessor latest -> ', propertyProcessor);

    return () => {
      console.log(
        'Unmounting property- ',
        propertyId,
        ' from screen-',
        screenName
      );
      removeScreenFromPropertyList();
      console.log('propertyProcessor after slicing -> ', propertyProcessor);
    };
  });

  const screenIndex = getLatestScreenIndex(screenName);
  if (screenIndex === -1) {
    propertyProcessor.push({
      screenName: screenName,
      propertyList: [],
    });
  }

  function getLatestScreenIndex(screen) {
    return propertyProcessor?.findIndex((obj) => obj.screenName === screen);
  }

  const removeScreenFromPropertyList = () => {
    propertyProcessor?.map((val, index) => {
      if (val.screenName === screenName) {
        propertyProcessor.splice(index, 1);
      }
    });
    if (!propertyProcessor.length && isListenerAdded) {
      dataReceivedListener?.remove();
      renderListerner?.remove();
      exceptionalListener?.remove();
      console.log(
        '\n---------------------------------------------------------\n'
      );
      console.log('@@@@ All the Listeners are removed ');
      isListenerAdded = false;
    }
  };

  if (
    !propertyProcessor[propertyProcessor.length - 1]?.propertyList
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
    propertyProcessor[propertyProcessor.length - 1].propertyList.push(obj);
  }

  if (!isListenerAdded) {
    // onDataReceived
    dataReceivedListener = eventEmitter.addListener(
      'onDataReceived',
      (event) => {
        const { targetViewId = '', campaignId = '' } = event;
        const payload = JSON.parse(event.payloadData);
        const weCampaignData = {
          targetViewId,
          campaignId,
          payload,
        };
        console.log(
          'onDataReceived - Event Listerner called ->',
          weCampaignData
        );
        if (propertyProcessor?.length) {
          propertyProcessor[propertyProcessor.length - 1]?.propertyList?.map(
            (val) => {
              if (val.propertyId === event.targetViewId) {
                if (val?.callbacks?.onDataReceived) {
                  val?.callbacks?.onDataReceived(weCampaignData);
                }
              }
            }
          );
        }
      }
    );

    // onRendered
    renderListerner = eventEmitter.addListener('onRendered', (event) => {
      const { targetViewId = '', campaignId = '', payloadData } = event;
      const payload = JSON.parse(payloadData);
      const weCampaignData = {
        targetViewId,
        campaignId,
        payload,
      };
      console.log(
        'index -onRendered - Event Listerner called ->',
        weCampaignData
      );

      if (propertyProcessor?.length) {
        propertyProcessor[propertyProcessor.length - 1]?.propertyList?.map(
          (val) => {
            if (val.propertyId === targetViewId) {
              val?.callbacks?.onRendered &&
                val?.callbacks?.onRendered(weCampaignData);
            }
          }
        );
      }
    });

    // onPlaceholderException
    exceptionalListener = eventEmitter.addListener(
      'onPlaceholderException',
      (event) => {
        console.log(
          'onPlaceholderException - Event Listerner called ->',
          event
        );
        const { targetViewId = '' } = event;

        if (propertyProcessor?.length) {
          propertyProcessor[propertyProcessor.length - 1]?.propertyList?.map(
            (val) => {
              if (val.propertyId === targetViewId) {
                val?.callbacks?.onPlaceholderException &&
                  val?.callbacks?.onPlaceholderException(event);
              }
            }
          );
        }
      }
    );
    console.log(
      '\n---------------------------------------------------------\n'
    );
    console.log('@@@@ Listeners Added ');
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
