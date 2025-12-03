import {
  NativeEventEmitter,
  NativeModules,
  requireNativeComponent,
  UIManager,
} from 'react-native';
import {COMPONENT_NAME} from '../utils/WEGConstants';

/**
 * Initialize WEPersonalizationBridge with architecture detection
 * Supports both New Architecture (TurboModules) and Legacy Architecture
 */
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

/**
 * Initialize WebEngage Personalization SDK
 * Works with both architectures
 */
export const initWePersonalization = () => {
  WEPersonalizationBridge.initWePersonalization();
}

/**
 * Event emitter for handling native events
 * Compatible with both TurboModules and Legacy modules
 */
export const eventEmitter = new NativeEventEmitter(WEPersonalizationBridge);

/**
 * WebEngage Personalization View Component
 * Supports both Fabric (New Architecture) and Legacy Architecture
 * Automatically detects and uses appropriate implementation
 */
let WebengagePersonalizationView;

console.log('JS - Fabric detection: global.nativeFabricUIManager =', global.nativeFabricUIManager);
console.log('JS - TurboModule detection: global.__turboModuleProxy =', global.__turboModuleProxy);
console.log('JS - COMPONENT_NAME =', COMPONENT_NAME);

if (global.nativeFabricUIManager) {
  // New Architecture - Use Fabric component
  console.log('JS - Attempting to load Fabric component...');
  try {
    const WEPersonalizationViewNativeComponent = require('./WEPersonalizationViewNativeComponent').default;
    console.log('JS - Fabric component loaded:', WEPersonalizationViewNativeComponent);
    WebengagePersonalizationView = WEPersonalizationViewNativeComponent;
    console.log('JS - ✅ Using Fabric View Component (NEW ARCHITECTURE)');
  } catch (e) {
    console.log('JS - ❌ Fabric View Component load failed:', e.message);
    console.log('JS - Error stack:', e.stack);
    console.log('JS - Falling back to Legacy View');
    // Fallback to legacy component
    WebengagePersonalizationView = UIManager.getViewManagerConfig(COMPONENT_NAME) != null ?
      requireNativeComponent(COMPONENT_NAME) :
      () => {
        console.error("LINKING_ERROR - Error While Linking View")
      };
  }
} else {
  // Legacy Architecture - Use traditional requireNativeComponent
  console.log('JS - Fabric not detected, using Legacy component');
  WebengagePersonalizationView = UIManager.getViewManagerConfig(COMPONENT_NAME) != null ?
    requireNativeComponent(COMPONENT_NAME) :
    () => {
      console.error("LINKING_ERROR - Error While Linking View")
    };
  console.log('JS - Using Legacy View Component (OLD ARCHITECTURE)');
}

export { WebengagePersonalizationView };
