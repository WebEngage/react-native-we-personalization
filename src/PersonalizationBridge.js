import {
  NativeEventEmitter,
  NativeModules,
  requireNativeComponent,
  UIManager,
} from 'react-native';
import { COMPONENT_NAME } from './WEGConstants';

const { PersonalizationBridge } = NativeModules;
export default PersonalizationBridge;

export const eventEmitter = new NativeEventEmitter(PersonalizationBridge);

export const WebengagePersonalizationView =
  UIManager.getViewManagerConfig(COMPONENT_NAME) != null
    ? requireNativeComponent(COMPONENT_NAME)
    : () => {
        throw new Error(LINKING_ERROR);
      };
