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
import VisibilitySensor from '@svanboxel/visibility-sensor-react-native';

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

  var myView = React.useRef(null);

  const [rectTop, setrectTop] = React.useState(0);
  const [rectBottom, setrectBottom] = React.useState(0);
  const [rectWidth, setrectWidth] = React.useState(0);

  const [isVisible, setisVisible] = React.useState(false);
  const [lastValue, setLastValue] = React.useState(false);

  React.useImperativeHandle(ref, () => ({
    onScroll: () => {
      myView.current.measure((x, y, width, height, pageX, pageY) => {
        // console.log('on scroll ', pageY);
        console.log(
          '^^^ ============= Starts for ',
          propertyId,
          ' =============='
        );
        console.log('^^^ ref -> ', ref);
        console.log('^^^ Component width is: ' + width);
        console.log('^^^ Component height is: ' + height);
        console.log('^^^ X offset to frame: ' + x);
        console.log('^^^ Y offset to frame: ' + y);
        console.log('^^^ X offset to page: ' + pageX);
        console.log('Y offset to page: ' + pageY);
        console.log(
          '^^^ ============= Ends for ',
          propertyId,
          ' =============='
        );
        setrectTop(pageY);
        setrectBottom(pageY + height);
        setrectWidth(pageX + width);
        isInView();
        console.log('^^^ \n \n \n \n \n');
      });
    },
  }));

  const isInView = () => {
    var window = Dimensions.get('window');
    var isVisible =
      rectBottom != 0 &&
      rectTop >= 0 &&
      rectBottom <= window.height &&
      rectWidth > 0 &&
      rectWidth <= window.width;
    console.log(
      '^^ isVisible ------- ',
      isVisible,
      rectBottom,
      rectTop,
      rectWidth,
      window.height,
      window.width,
      propertyId
    );

    if (lastValue !== isVisible) {
      setLastValue(isVisible);
      console.log('^^ onChange of val ------- ', isVisible);
    }
  };

  // TODO - Remove All Listeners when screen is changed - also delete propertyList and create new
  // eventEmitter.removeAllListeners();

  // pushing properties inside an array of listeners

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

  const handleImageVisibility = (visible) => {
    // handle visibility change
    console.log('handleImageVisibility ----', visible, ' for ', propertyId);
    setisVisible(visible);
  };
  const customProps = {
    ...props,
    isVisibleInViewport: isVisible,
  };

  return <WebengagePersonalizationView {...customProps} ref={myView} />;
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
