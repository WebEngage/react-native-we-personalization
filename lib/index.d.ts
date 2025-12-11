import React from 'react';
import { ViewStyle } from 'react-native';

// Campaign callback types
export interface WECampaignData {
  [key: string]: any;
}

export interface WECampaignCallbacks {
  onCampaignPrepared?: (data: WECampaignData) => void;
  onCampaignShown?: (data: WECampaignData) => void;
  onCampaignClicked?: (data: WECampaignData) => void;
  onCampaignException?: (data: WECampaignData) => void;
}

// Placeholder callback types
export interface WEPlaceholderData {
  [key: string]: any;
}

// WEInlineWidget props
export interface WEInlineWidgetProps {
  androidPropertyId: string;
  iosPropertyId: number;
  screenName: string;
  onRendered?: (data: any) => void;
  onDataReceived?: (data: WEPlaceholderData) => void;
  onPlaceholderException?: (error: any) => void;
  style: ViewStyle;
}

// WEInlineWidget component
export declare const WEInlineWidget: React.ComponentType<WEInlineWidgetProps>;

// Bridge functions
export declare function initWePersonalization(): void;
export declare function getArchitectureStatus(): { turboModule: boolean; fabric: false; };

// Campaign callback functions
export declare function registerWECampaignCallback(callbacks: WECampaignCallbacks): void;
export declare function deregisterWECampaignCallback(): void;

// Placeholder callback functions
export declare function registerWEPlaceholderCallback(
  androidPropertyId: string,
  iosPropertyId: number,
  screenName: string,
  onDataReceived: (data: WEPlaceholderData) => void,
  onPlaceholderException: (error: any) => void
): void;

export declare function deregisterWEPlaceholderCallback(
  androidPropertyId: string,
  iosPropertyId: number,
  screenName: string
): void;

// Tracking functions
export declare function trackClick(propertyId: string, attributes?: Record<string, any>): void;
export declare function trackImpression(propertyId: string, attributes?: Record<string, any>): void;

// Native module spec
export interface Spec {
  initWePersonalization(): void;
  registerProperty(tagName: string, screenName: string): void;
  deregisterProperty(tagName: string): void;
  registerWECampaignCallback(): void;
  deregisterWECampaignCallback(): void;
  trackImpression(propertyId: string, attributes: Record<string, any>): void;
  trackClick(propertyId: string, attributes: Record<string, any>): void;
}

export { Spec as NativeWEPersonalizationBridgeSpec };