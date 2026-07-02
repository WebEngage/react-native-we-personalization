/**
 * Jest setup file for react-native-we-personalization tests
 * 
 * Each test file mocks 'react-native' individually since the library
 * only uses Platform, NativeModules, NativeEventEmitter, UIManager,
 * and requireNativeComponent from React Native.
 */

// Suppress console.error in tests unless needed
// jest.spyOn(console, 'error').mockImplementation(() => {});
