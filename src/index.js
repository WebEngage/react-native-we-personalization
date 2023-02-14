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
const PersonalizationBridge = NativeModules.PersonalizationBridge;

const eventEmitter = new NativeEventEmitter(PersonalizationBridge);
let dataReceivedListener = null;
let renderListerner = null;
let exceptionalListener = null;
let isListenerAdded = false;
let isCustomListenerAdded = false;
const propertyProcessor = [];
const customPropertyList = [];

let customOnRenderedListener = null;
let customExceptionListener = null;

export const registerCustomPlaceHolder = (
  propertyId,
  screenName,
  onDataReceivedCb,
  onPlaceholderExceptionCb
) => {
  PersonalizationBridge.registerCallback(propertyId);
  registerPropertyList(
    customPropertyList,
    screenName,
    propertyId,
    onDataReceivedCb,
    null,
    onPlaceholderExceptionCb
  );

  console.log(
    'registerCustomPlaceHolder registered customPropertyList',
    customPropertyList
  );
  if (!isCustomListenerAdded) {
    customOnRenderedListener = eventEmitter.addListener(
      'onCustomDataReceived',
      (data) => {
        console.log('onCustomDataReceived list', data);
        sendOnDataReceivedEvent(customPropertyList, data);
      }
    );
    customExceptionListener = eventEmitter.addListener(
      'onCustomPlaceholderException',
      (data) => {
        console.log('onCustomPlaceholderException list', data);
        sendOnException(customPropertyList, data);
      }
    );
    isCustomListenerAdded = true;
  }
};

const removeScreenFromPropertyList = (list, screenName) => {
  list?.map((val, index) => {
    if (val.screenName === screenName) {
      list.splice(index, 1);
    }
  });
  if (!list.length && isListenerAdded) {
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

const getPropertyDetails = (list, weCampaignData) => {
  let res = null;
  const { targetViewId = '' }  = weCampaignData
      if (list?.length) {
        list[list.length - 1]?.propertyList?.map(
          (val) => {
            if (val.propertyId === targetViewId) {
              res = val;
            }
          }
        );
      }
      return res;
}

const sendOnDataReceivedEvent = (list, data) => {
  const { targetViewId = '', campaignId = '', payloadData = '' } = data;
  const payload = JSON.parse(payloadData);
  const weCampaignData = {
    targetViewId,
    campaignId,
    payload,
  };
  const propertyItem = getPropertyDetails(list, weCampaignData);
  console.log('onDataReceived! - Event Listener called ->', weCampaignData);

        if(propertyItem?.callbacks?.onDataReceived) {
          propertyItem?.callbacks?.onDataReceived(weCampaignData);
        }
};


const sendOnRendered = (list ,data) => {
  const { targetViewId = '', campaignId = '', payloadData } = data;
      const payload = JSON.parse(payloadData);
      const weCampaignData = {
        targetViewId,
        campaignId,
        payload,
      };
      console.log(
        'onRendered - Event Listener called ->',
        weCampaignData
      );

      const propertyItem = getPropertyDetails(list, weCampaignData);
        if(propertyItem?.callbacks?.onRendered) {
          propertyItem?.callbacks?.onRendered(weCampaignData);
        }
}

const sendOnException = (list, data) => {
  console.log(
    'onPlaceholderException - Event Listerner called ->',
    data
  );
  const { targetViewId = '' } = data;
  const weCampaignData = {
    targetViewId
  };
  const propertyItem = getPropertyDetails(list, weCampaignData);
        if(propertyItem?.callbacks?.onPlaceholderException) {
          propertyItem?.callbacks?.onPlaceholderException(data);
        }
}

export const unRegisterCustomPlaceHolder = (propertyId, screen) => {
  console.log('unRegisterCustomPlaceHolder! - Event Listener called ->');

  PersonalizationBridge.unRegisterCallback(propertyId);
  removeScreenFromPropertyList(customPropertyList, screen);
  customOnRenderedListener?.remove();
  customExceptionListener?.remove();
  isCustomListenerAdded = false;
};

function getLatestScreenIndex(screen, list) {
  return list?.findIndex((obj) => obj.screenName === screen);
}

const registerPropertyList = (
  list,
  screenName,
  propertyId,
  onDataReceived = null,
  onRendered = null,
  onPlaceholderException = null
) => {
  const screenIndex = getLatestScreenIndex(screenName, list);
  if (screenIndex === -1) {
    list.push({
      screenName: screenName,
      propertyList: [],
    });
  }
  if (
    !list[list.length - 1]?.propertyList
      .flatMap((curr) => curr.propertyId)
      .includes(propertyId)
  ) {
    let obj = {};
    obj.propertyId = propertyId;
    obj.callbacks = {
      onRendered: onRendered,
      onPlaceholderException: onPlaceholderException,
      onDataReceived: onDataReceived,
    };
    list[list.length - 1].propertyList.push(obj);
  }
};

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


  React.useEffect(() => {
    console.log('propertyProcessor latest -> ', propertyProcessor);

    return () => {
      console.log(
        'Unmounting property- ',
        propertyId,
        ' from screen-',
        screenName
      );
      removeScreenFromPropertyList(propertyProcessor, screenName);
      console.log('propertyProcessor after slicing -> ', propertyProcessor);
    };
  });

  registerPropertyList(
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
      sendOnRendered(propertyProcessor, data)
    });

    // onPlaceholderException
    exceptionalListener = eventEmitter.addListener(
      'onPlaceholderException',
      (data) => {
        sendOnException(propertyProcessor, data)
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
