import {
  NativeEventEmitter,
  NativeModules,
  requireNativeComponent,
  UIManager,
} from 'react-native';
import {COMPONENT_NAME} from '../utils/WEGConstants';

const {WEPersonalizationBridge} = NativeModules;
export default WEPersonalizationBridge;

export const initWePersonalization = () => {
  WEPersonalizationBridge.initWePersonalization();
}

export const eventEmitter = new NativeEventEmitter(WEPersonalizationBridge);

export const WebengagePersonalizationView =
  UIManager.getViewManagerConfig(COMPONENT_NAME) != null ?
    requireNativeComponent(COMPONENT_NAME) :
    () => {
      throw new Error('LINKING_ERROR');
    };
