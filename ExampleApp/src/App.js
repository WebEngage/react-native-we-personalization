import * as React from 'react';
import {enableDevMode} from 'react-native-we-personalization/src/utils/weLogs';
import LoginScreen from './LoginScreen';
import Navigation from './Navigation';
import {getValueFromAsyncStorage} from './Utils';
import {initWebEngage, webengageInstance} from './Utils/WebEngageManager';
import { LogBox, PermissionsAndroid, Platform } from 'react-native';
import messaging from '@react-native-firebase/messaging';
LogBox.ignoreAllLogs()

export default function App() {
  const [isUserLoggedIn, setIsUserLoggedIn] = React.useState(false);
  const [continueAsGuest, setContinueAsGuest] = React.useState(false);
  var flag = false

  initWebEngage();
  const userNameRef = React.useRef(null);

  React.useEffect(() => {
    const unsubscribe = messaging().onMessage(async remoteMessage => {
      console.log('App: Firebase A new FCM message arrived!', JSON.stringify(remoteMessage));
    });
    getToken()

    return unsubscribe;
  }, []);

  const getToken = async() => {
    await messaging().registerDeviceForRemoteMessages();
    const token = await messaging().getToken();
    console.log("App: Firebase Token ",token)
  }

  React.useEffect(() => {
    webengageInstance.push.onClick(function(notificationData) {
      console.log("App: WebEngage-  push-notifcation clicked with deeplink: " + notificationData["deeplink"]);
    });
    requestUserPermission()
    getInitialMessages()
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

  async function requestUserPermission() {
    const authStatus = await messaging().requestPermission();
    const enabled =
      authStatus === messaging.AuthorizationStatus.AUTHORIZED ||
      authStatus === messaging.AuthorizationStatus.PROVISIONAL;
  
    if (enabled) {
      console.log('Authorization status:', authStatus);
    }
  }

  const getInitialMessages = () => {
    messaging()
      .getInitialNotification()
      .then(remoteMessage => {
        if (remoteMessage) {
          console.log(
            'App: Firebase - Notification caused app to open from quit state:',
            remoteMessage.notification,
          );
        } else {
          console.log("App: Firebase - Launching App without any notifications!!")
        }
      });

  }


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
