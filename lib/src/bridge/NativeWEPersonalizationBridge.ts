import type { TurboModule } from 'react-native/Libraries/TurboModule/RCTExport';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  initWePersonalization(): void;
  registerProperty(propertyId: string | number, screenName: string): void;
  deregisterProperty(propertyId: string | number): void;
  registerWECampaignCallback(): void;
  deregisterWECampaignCallback(): void;
  trackClick(propertyId: string | number, attributes?: Object): void;
  trackImpression(propertyId: string | number, attributes?: Object): void;
  
  // NativeEventEmitter methods for the New Architecture
  addListener: (eventType: string) => void;
  removeListeners: (count: number) => void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('WEPersonalizationBridge');
