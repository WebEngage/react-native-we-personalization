# WebEngage Personalization - Fabric + Legacy Architecture Support

## Overview
Updated the react-native-we-personalization library to support both React Native's New Architecture (Fabric) and Legacy Architecture, following the pattern established in the example-component.

## Changes Made

### 1. Build Configuration (build.gradle)
- ✅ Already configured with `isNewArchitectureEnabled()` function
- ✅ Conditional sourceSets for newarch/oldarch folders
- ✅ Fabric plugin applied when New Architecture is enabled
- ✅ Codegen configuration for WEPersonalizationView component

### 2. Common Implementation
**Created:** `android/src/main/java/com/webengage/we_personalization_rn/WEPersonalizationViewManagerImpl.java`
- Shared logic between New and Old Architecture ViewManagers
- Handles WEInlineWidget creation and property updates
- Initializes WebEngage SDK settings
- Similar pattern to `ColoredViewManagerImpl.java` in example-component

### 3. New Architecture Support (Fabric)

#### Module
**Updated:** `android/src/newarch/java/com/webengage/we_personalization_rn/WEPersonalizationModule.kt`
- Extends `NativeWEPersonalizationBridgeSpec` (TurboModule)
- Implements all bridge methods for New Architecture

#### ViewManager
**Updated:** `android/src/newarch/java/com/webengage/we_personalization_rn/WEPersonalizationViewManager.kt`
- Uses common implementation from `WEPersonalizationViewManagerImpl`
- Fabric-compatible ViewManager
- Handles React props and delegates to common implementation

#### TurboModule Spec
- Uses codegen-generated `NativeWEPersonalizationBridgeSpec` class
- Generated from TypeScript interface in `NativeWEPersonalizationBridge.ts`

### 4. Legacy Architecture Support

#### Module
**Maintained:** `android/src/oldarch/java/com/webengage/we_personalization_rn/WEPersonalizationModule.kt`
- Extends `ReactContextBaseJavaModule` (Legacy)
- Uses `@ReactMethod` annotations
- Maintains backward compatibility

#### ViewManager
**Created:** `android/src/oldarch/java/com/webengage/we_personalization_rn/WEPersonalizationViewManager.kt`
- Uses common implementation from `WEPersonalizationViewManagerImpl`
- Legacy-compatible ViewManager
- Handles React props and delegates to common implementation

### 5. Package Configuration
**Updated:** `android/src/main/java/com/webengage/we_personalization_rn/WEPersonalizationPackage.java`
- Removed dependency on main ViewManager
- Now uses architecture-specific ViewManagers from newarch/oldarch folders

### 6. JavaScript Interface (Already Configured)
- ✅ `WEPersonalizationViewNativeComponent.ts` - Fabric component spec
- ✅ `NativeWEPersonalizationBridge.ts` - TurboModule interface
- ✅ `WEPersonalizationBridge.js` - Architecture detection and fallback logic
- ✅ `WEInlineWidget.js` - Component with architecture-aware logging

## Architecture Detection

### JavaScript Side
The library automatically detects the runtime architecture:

```javascript
// TurboModule detection
if (global.__turboModuleProxy) {
  // Use New Architecture (TurboModule)
} else {
  // Use Legacy Architecture (NativeModules)
}

// Fabric View detection  
if (global.nativeFabricUIManager) {
  // Use Fabric View Component
} else {
  // Use Legacy View Component
}
```

### Android Side
Build-time detection using gradle:
```gradle
sourceSets {
  main {
    if (isNewArchitectureEnabled()) {
      java.srcDirs += ['src/newarch/java']
    } else {
      java.srcDirs += ['src/oldarch/java']
    }
  }
}
```

## File Structure
```
android/src/
├── main/java/com/webengage/we_personalization_rn/
│   ├── WEPersonalizationViewManagerImpl.java    # Common implementation
│   └── WEPersonalizationPackage.java            # Updated package
├── newarch/java/com/webengage/we_personalization_rn/
│   ├── WEPersonalizationModule.kt               # Fabric module
│   └── WEPersonalizationViewManager.kt          # Fabric ViewManager
└── oldarch/java/com/webengage/we_personalization_rn/
    ├── WEPersonalizationModule.kt               # Legacy module  
    └── WEPersonalizationViewManager.kt          # Legacy ViewManager
```

## Benefits
1. **Seamless Migration** - Works with both architectures without code changes
2. **Future-Proof** - Ready for New Architecture adoption
3. **Backward Compatible** - Maintains support for existing Legacy apps
4. **Shared Logic** - Common implementation reduces code duplication
5. **Architecture Detection** - Automatic runtime detection and fallback

## Testing
The library will automatically use the appropriate implementation based on:
- Build configuration (`newArchEnabled` property)
- Runtime detection (TurboModule/Fabric availability)
- Graceful fallback to Legacy implementation if New Architecture fails

## Usage
No changes required for existing users. The library automatically detects and uses the appropriate architecture implementation.