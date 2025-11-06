import {
  NativeEventEmitter,
  NativeModules,
  requireNativeComponent,
  UIManager,
} from 'react-native';
import {COMPONENT_NAME} from '../utils/WEGConstants';

let WEPersonalizationBridge;

if (global.__turboModuleProxy) {
  // New Architecture runtime is enabled
  console.log('JS - Running with New Architecture, attempting TurboModule load');
  try {
    const NativeWEPersonalizationBridge = require('./NativeWEPersonalizationBridge').default;
    if (NativeWEPersonalizationBridge && typeof NativeWEPersonalizationBridge.initWePersonalization === 'function') {
      console.log('JS - TurboModule loaded successfully (NEW ARCHITECTURE)');
      WEPersonalizationBridge = NativeWEPersonalizationBridge;
    } else {
      throw new Error('TurboModule not available');
    }
  } catch (e) {
    console.log('JS - TurboModule load failed, falling back to Bridge (OLD ARCHITECTURE):', e.message);
    WEPersonalizationBridge = NativeModules.WEPersonalizationBridge;
  }
} else {
  // Old Architecture runtime
  console.log('JS - Running with Old Architecture, using Bridge');
  WEPersonalizationBridge = NativeModules.WEPersonalizationBridge;
}

export default WEPersonalizationBridge;

export const initWePersonalization = () => {
  WEPersonalizationBridge.initWePersonalization();
}

export const eventEmitter = new NativeEventEmitter(WEPersonalizationBridge);

export const WebengagePersonalizationView =
  UIManager.getViewManagerConfig(COMPONENT_NAME) != null ?
    requireNativeComponent(COMPONENT_NAME) :
    () => {
      console.error("LINKING_ERROR - Error While Linking View")
    };
