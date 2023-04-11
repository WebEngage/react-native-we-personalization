import WebEngage from 'react-native-webengage';

export let webengageInstance = null;

export const initWebEngage = () => {
  webengageInstance = new WebEngage();
};
