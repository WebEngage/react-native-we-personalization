import * as React from 'react';
import {enableDevMode} from 'react-native-we-personalization/src/utils/weLogs';
import LoginScreen from './LoginScreen';
import Navigation from './Navigation';
import {getValueFromAsyncStorage} from './Utils';
import {initWebEngage, webengageInstance} from './Utils/WebEngageManager';
import { LogBox, PermissionsAndroid, Platform } from 'react-native';
LogBox.ignoreAllLogs()

export default function App() {
  const [isUserLoggedIn, setIsUserLoggedIn] = React.useState(false);
  const [continueAsGuest, setContinueAsGuest] = React.useState(false);

  initWebEngage();
  const userNameRef = React.useRef(null);

  React.useEffect(() => {
    webengageInstance.push.onClick(function(notificationData) {
        console.log("App: push-notiifcation clicked with deeplink: " + notificationData["deeplink"]);
    });
  }, []);

  React.useEffect(() => {
    (async () => {
      const name = await getValueFromAsyncStorage('userName');
      if (name) {
        setIsUserLoggedIn(true);
      }
      userNameRef.current = name;
      enableDevMode();
    })();
    if(Platform.OS == "android") {
      webengageInstance.user.setDevicePushOptIn(true)
    }
  }, []);


  const updateLoginDetails = (loginState) => {
    setIsUserLoggedIn(loginState);
  };

  const updateGuestState = () => {
    setContinueAsGuest(true);
  };

  if (isUserLoggedIn) {
    return <Navigation />;
  } else {
    if (!continueAsGuest) {
      return (
        <LoginScreen
          isUserLoggedIn
          updateLoginDetails={updateLoginDetails}
          updateGuestState={updateGuestState} />
      );
    } else {
      return <Navigation />;
    }
  }
}
