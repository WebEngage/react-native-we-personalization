import {
  requireNativeComponent,
  UIManager,
  Platform,
  ViewStyle,
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

type WebengagePersonalizationProps = {
  color: string;
  style: ViewStyle;
};

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
    ? requireNativeComponent<WebengagePersonalizationProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };

export function multiply(a: number, b: number): Promise<number> {
  return PersonalizationBridge.multiply(a, b);
}

// Below code is example for multiple call
export function add(a: number, b: number): Promise<number> {
  return PersonalizationBridge.add(a, b);
}
