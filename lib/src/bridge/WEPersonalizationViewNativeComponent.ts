import type { ViewProps } from 'react-native';
import type { HostComponent } from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

/**
 * Props interface for WEPersonalizationView Fabric component
 * Defines the properties that can be passed to the native view component
 */
export interface WEPersonalizationViewProps extends ViewProps {
  /**
   * Unique identifier for the personalization property
   * Used to identify which campaign/content to display
   */
  propertyId: string;
  
  /**
   * Screen name where the widget is displayed
   * Used for analytics and campaign targeting
   */
  screenName: string;
}

/**
 * Fabric-compatible native component specification for WEPersonalizationView
 * This component will be used in New Architecture (Fabric) runtime
 * Falls back to legacy implementation in Old Architecture
 */
export default codegenNativeComponent<WEPersonalizationViewProps>(
  'WEPersonalizationView'
) as HostComponent<WEPersonalizationViewProps>;