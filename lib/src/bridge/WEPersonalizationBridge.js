import {
  NativeEventEmitter,
  NativeModules,
  requireNativeComponent,
  UIManager,
} from 'react-native';
import { COMPONENT_NAME } from '../utils/WEGConstants';
import { weLogs } from '../utils/weLogs';

/**
 * Initialize WEPersonalizationBridge with architecture detection
 * Supports both New Architecture (TurboModules) and Legacy Architecture
 */
let WEPersonalizationBridge = null;

try {
  if (global.__turboModuleProxy) {
    try {
      const NativeWEPersonalizationBridge = require('./NativeWEPersonalizationBridge').default;
      if (NativeWEPersonalizationBridge && typeof NativeWEPersonalizationBridge.initWePersonalization === 'function') {
        WEPersonalizationBridge = NativeWEPersonalizationBridge;
        weLogs('TurboModule loaded (New Architecture)');
      } else {
        throw new Error('TurboModule not available');
      }
    } catch (e) {
      weLogs('TurboModule load failed, falling back to Bridge:', e.message);
      WEPersonalizationBridge = NativeModules.WEPersonalizationBridge;
    }
  } else {
    WEPersonalizationBridge = NativeModules.WEPersonalizationBridge;
    weLogs('Using Bridge (Old Architecture)');
  }
} catch (error) {
  console.error('[WEPersonalization] Failed to initialize bridge:', error);
}

if (!WEPersonalizationBridge) {
  throw new Error(
    '[WEPersonalization] Native module not found. Please ensure the library is properly linked.'
  );
}

export default WEPersonalizationBridge;

/**
 * Get current architecture status
 * @returns {Object} Architecture detection status
 */
export const getArchitectureStatus = () => ({
  turboModule: !!global.__turboModuleProxy,
  fabric: !!global.nativeFabricUIManager,
});

/**
 * Initialize WebEngage Personalization SDK
 * Works with both architectures
 */
export const initWePersonalization = () => {
  try {
    WEPersonalizationBridge?.initWePersonalization();
  } catch (error) {
    weLogs('ERROR: initWePersonalization failed:', error);
  }
};

/**
 * Event emitter for handling native events
 * Compatible with both TurboModules and Legacy modules
 */
export const eventEmitter = WEPersonalizationBridge
  ? new NativeEventEmitter(WEPersonalizationBridge)
  : null;

/**
 * WebEngage Personalization View Component
 * Supports both Fabric (New Architecture) and Legacy Architecture
 * Automatically detects and uses appropriate implementation
 */
let WebengagePersonalizationView = null;

try {
  if (global.nativeFabricUIManager) {
    try {
      const WEPersonalizationViewNativeComponent = require('./WEPersonalizationViewNativeComponent').default;
      WebengagePersonalizationView = WEPersonalizationViewNativeComponent;
      weLogs('Using Fabric View Component (New Architecture)');
    } catch (e) {
      weLogs('Fabric View Component load failed, falling back to Legacy:', e.message);
      if (UIManager.getViewManagerConfig(COMPONENT_NAME) != null) {
        WebengagePersonalizationView = requireNativeComponent(COMPONENT_NAME);
      }
    }
  } else {
    if (UIManager.getViewManagerConfig(COMPONENT_NAME) != null) {
      WebengagePersonalizationView = requireNativeComponent(COMPONENT_NAME);
      weLogs('Using Legacy View Component (Old Architecture)');
    }
  }
} catch (error) {
  console.error('[WEPersonalization] Failed to initialize view component:', error);
}

if (!WebengagePersonalizationView) {
  WebengagePersonalizationView = () => {
    console.error(
      '[WEPersonalization] View component not found. Please ensure the library is properly linked.'
    );
    return null;
  };
}

export { WebengagePersonalizationView };
