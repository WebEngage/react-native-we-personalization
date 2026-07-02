import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

/**
 * Native module specification for WebEngage Personalization Bridge
 * Compatible with React Native's TurboModule/Codegen system
 */
export interface Spec extends TurboModule {
  readonly initWePersonalization: () => void;
  readonly registerProperty: (propertyId: string, screenName: string) => void;
  readonly deregisterProperty: (propertyId: string) => void;
  readonly registerWECampaignCallback: () => void;
  readonly deregisterWECampaignCallback: () => void;
  readonly trackClick: (propertyId: string, attributes?: Object) => void;
  readonly trackImpression: (propertyId: string, attributes?: Object) => void;
  readonly addListener: (eventType: string) => void;
  readonly removeListeners: (count: number) => void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('WEPersonalizationBridge');
