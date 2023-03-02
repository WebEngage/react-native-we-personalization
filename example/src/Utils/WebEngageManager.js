import WebEngage from 'react-native-webengage';

export var webengageInstance = null;

export const initWebEngage = () => {
  webengageInstance = new WebEngage();
};
