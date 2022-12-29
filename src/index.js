import {
  requireNativeComponent,
  UIManager,
  Platform,
  NativeModules,
} from 'react-native';

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

// type WebengagePersonalizationProps = {
//   color: string;
//   style: ViewStyle;
// };

const ComponentName = 'WebengagePersonalizationView';

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
