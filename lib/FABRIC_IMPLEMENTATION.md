# Fabric Implementation Guide

## Overview

This document outlines the Fabric (New Architecture) implementation for the WebEngage Personalization React Native SDK. The implementation provides seamless support for both New Architecture (Fabric) and Legacy Architecture with automatic detection and fallback mechanisms.

## Architecture Support

### Automatic Detection
The SDK automatically detects the React Native architecture at runtime:
- **New Architecture (Fabric)**: Uses TurboModules and Fabric components
- **Legacy Architecture**: Falls back to traditional NativeModules and requireNativeComponent

### Detection Logic
```javascript
if (global.__turboModuleProxy) {
  // New Architecture detected - use Fabric components
} else {
  // Legacy Architecture - use traditional components
}
```

## Implementation Details

### JavaScript Layer

#### 1. Bridge Architecture Detection (`WEPersonalizationBridge.js`)
- Detects runtime architecture using `global.__turboModuleProxy`
- Loads appropriate TurboModule or NativeModule
- Provides unified interface for both architectures

#### 2. View Component Support (`WEPersonalizationViewNativeComponent.ts`)
- Fabric-compatible view component specification
- Uses `codegenNativeComponent` for type safety
- Defines props interface for native components

#### 3. Widget Component (`WEInlineWidget.js`)
- Architecture-agnostic React component
- Automatic architecture detection and logging
- Consistent API across both architectures

### Android Implementation

#### 1. Shared Implementation (`WEPersonalizationViewManagerImpl.kt`)
- Core business logic shared between architectures
- WebEngage SDK integration
- Property and style management

#### 2. Fabric View Manager (`WEPersonalizationViewManager.kt`)
- Implements CodeGen-generated interfaces
- Delegates to shared implementation
- Fabric-specific property handling

#### 3. Legacy View Manager (`WEPersonalizationViewManager.java`)
- Updated to use shared implementation
- Maintains backward compatibility
- Consistent behavior with Fabric

### iOS Implementation

#### 1. Shared Implementation (`WEPersonalizationViewManagerImpl.swift`)
- Core business logic for both architectures
- WEInlineWidget creation and management
- Property and style updates

#### 2. Fabric View Manager (`WEPersonalizationFabricViewManager.swift`)
- Fabric-compatible view manager
- Conditional compilation with `#if RCT_NEW_ARCH_ENABLED`
- CodeGen integration

#### 3. Legacy View Manager (`WEPersonalizationViewManager.swift`)
- Updated to use shared implementation
- Maintains existing API compatibility
- Consistent behavior with Fabric

## Usage

### Basic Usage (Same for Both Architectures)
```javascript
import { WEInlineWidget } from 'react-native-we-personalization';

const MyComponent = () => {
  return (
    <WEInlineWidget
      androidPropertyId="android_property_123"
      iosPropertyId={123}
      screenName="home_screen"
      onRendered={(data) => console.log('Rendered:', data)}
      onDataReceived={(data) => console.log('Data received:', data)}
      onPlaceholderException={(error) => console.log('Error:', error)}
      style={{ width: 300, height: 200 }}
    />
  );
};
```

### Architecture Detection
The SDK automatically logs which architecture is being used:
```
JS - Running with New Architecture, attempting TurboModule load
JS - TurboModule loaded successfully (NEW ARCHITECTURE)
JS - Using Fabric View Component (NEW ARCHITECTURE)
WEInlineWidget: Running on New Architecture (Fabric)
```

## Key Features

### 1. Seamless Migration
- No code changes required for existing implementations
- Automatic fallback to Legacy architecture if Fabric fails
- Consistent API across both architectures

### 2. Type Safety
- TypeScript definitions for TurboModule specs
- CodeGen integration for native components
- Proper prop type validation

### 3. Performance Optimization
- Fabric's synchronous native calls
- Reduced bridge overhead
- Better memory management

### 4. Error Handling
- Graceful fallback mechanisms
- Comprehensive error logging
- Architecture-specific error messages

## Debugging

### Architecture Detection Logs
The SDK provides detailed logging for debugging:
- Architecture detection results
- Component loading status
- Fallback mechanism activation
- Property updates and lifecycle events

### Common Issues

#### 1. TurboModule Not Found
```
JS - TurboModule load failed, falling back to Bridge (OLD ARCHITECTURE)
```
**Solution**: Ensure CodeGen has run and native modules are properly linked.

#### 2. Fabric View Component Load Failed
```
JS - Fabric View Component load failed, falling back to Legacy View
```
**Solution**: Check that Fabric is properly enabled and view managers are registered.

#### 3. Linking Errors
```
LINKING_ERROR - Error While Linking View
```
**Solution**: Verify native module linking and rebuild the application.

## Migration Guide

### From Legacy to Fabric
1. Enable New Architecture in your React Native app
2. Rebuild the application
3. The SDK will automatically detect and use Fabric components
4. No code changes required in JavaScript

### Testing Both Architectures
1. Test with New Architecture enabled
2. Test with New Architecture disabled
3. Verify consistent behavior across both modes
4. Check architecture detection logs

## CodeGen Configuration

The package.json includes proper CodeGen configuration:
```json
{
  "codegenConfig": {
    "name": "WEPersonalizationSpec",
    "type": "all",
    "jsSrcsDir": "src/bridge",
    "android": {
      "javaPackageName": "com.webengage.we_personalization_rn"
    },
    "ios": {
      "moduleName": "WEPersonalizationSpec"
    }
  }
}
```

## Best Practices

### 1. Always Use the Exported Component
```javascript
import { WEInlineWidget } from 'react-native-we-personalization';
```

### 2. Handle Architecture-Specific Behavior
The SDK handles architecture differences internally, but you can detect the architecture if needed:
```javascript
const isNewArch = global.__turboModuleProxy != null;
```

### 3. Error Handling
Always implement error callbacks:
```javascript
<WEInlineWidget
  onPlaceholderException={(error) => {
    console.error('Personalization error:', error);
  }}
/>
```

### 4. Performance Monitoring
Monitor the architecture being used in production:
```javascript
const architecture = global.__turboModuleProxy ? 'Fabric' : 'Legacy';
analytics.track('architecture_used', { architecture });
```

## Troubleshooting

### Build Issues
1. Clean and rebuild the project
2. Ensure CodeGen has run successfully
3. Check native module registration
4. Verify Fabric is properly enabled

### Runtime Issues
1. Check architecture detection logs
2. Verify fallback mechanisms are working
3. Test with both architectures
4. Monitor error callbacks

### Performance Issues
1. Verify Fabric is being used when enabled
2. Check for unnecessary re-renders
3. Monitor native bridge calls
4. Profile memory usage

## Future Considerations

### 1. Fabric-Only Features
Future versions may include Fabric-specific optimizations:
- Synchronous native calls
- Better animation support
- Improved layout calculations

### 2. Legacy Deprecation
While Legacy support will be maintained, new features may be Fabric-first:
- Enhanced type safety
- Better developer experience
- Performance improvements

### 3. Migration Path
A clear migration path will be provided when Legacy support is eventually deprecated:
- Migration tools
- Compatibility layers
- Gradual migration support