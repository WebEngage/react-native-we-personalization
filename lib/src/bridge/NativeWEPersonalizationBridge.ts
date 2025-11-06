import type { TurboModule } from 'react-native/Libraries/TurboModule/RCTExport';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  initWePersonalization(): void;
  registerProperty(propertyId: string, screenName: string): void;
  deregisterProperty(propertyId: string): void;
  registerWECampaignCallback(): void;
  deregisterWECampaignCallback(): void;
  trackClick(propertyId: string, attributes?: Object): void;
  trackImpression(propertyId: string, attributes?: Object): void;
  
  // NativeEventEmitter methods for the New Architecture
  addListener: (eventType: string) => void;
  removeListeners: (count: number) => void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('WEPersonalizationBridge');