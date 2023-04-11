export const COMPONENT_NAME = 'WEPersonalizationView';

export const LINKING_ERROR =
  `The package 'react-native-webengage-personalization' 
  doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ios: '- You have run \'pod install\'\n', default: ''}) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';
