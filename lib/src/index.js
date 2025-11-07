/**
 * WebEngage Personalization React Native SDK
 * 
 * This SDK provides comprehensive support for both React Native's New Architecture (Fabric)
 * and Legacy Architecture, ensuring seamless integration across all React Native versions.
 * 
 * Key Features:
 * - Cross-architecture compatibility (Fabric + Legacy)
 * - Cross-platform support (iOS + Android)
 * - WebEngage Personalization inline widgets
 * - Campaign callbacks and event tracking
 * - Automatic architecture detection and fallback
 */

import WEInlineWidget from './view/WEInlineWidget';
import {
  registerWECampaignCallback,
  deregisterWECampaignCallback,
} from './callbacks/WECampaignData';
import {
  registerWEPlaceholderCallback,
  deregisterWEPlaceholderCallback,
  trackClick,
  trackImpression,
} from './callbacks/WEPlaceHolder';

import { initWePersonalization } from './bridge/WEPersonalizationBridge';

/**
 * Main exports for WebEngage Personalization SDK
 * 
 * WEInlineWidget - The main component for displaying personalized content
 * initWePersonalization - Initialize the WebEngage Personalization SDK
 * Campaign and Placeholder callbacks - For handling personalization events
 * Tracking functions - For analytics and user interaction tracking
 */
export {
  WEInlineWidget,
  initWePersonalization,
  registerWECampaignCallback,
  deregisterWECampaignCallback,
  registerWEPlaceholderCallback,
  deregisterWEPlaceholderCallback,
  trackClick,
  trackImpression,
};

