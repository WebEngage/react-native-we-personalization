import * as React from 'react';

import LoginScreen from './LoginScreen';
import Navigation from './Navigation';
import { getValueFromAsyncStorage } from './Utils';
import { initWebEngage } from './Utils/WebEngageManager';
import { Alert, LogBox } from 'react-native';
import { initWePersonalization, registerWECampaignCallback } from 'react-native-we-personalization';
LogBox.ignoreAllLogs()

export default function App() {
  const [isUserLoggedIn, setIsUserLoggedIn] = React.useState(false);
  const [continueAsGuest, setContinueAsGuest] = React.useState(false);

  initWebEngage();
  initWePersonalization();
  const userNameRef = React.useRef(null);

  React.useEffect(() => {
    (async () => {
      const name = await getValueFromAsyncStorage('userName');
      if (name) {
        setIsUserLoggedIn(true);
      }
      userNameRef.current = name;
      enableDevMode();
    })();
  }, []);

  React.useEffect(() => {
    Alert.alert("App Mounted: Registering campaign callbacks");
    console.log('Arch: js:  App.js: Registering campaign callbacks');
    const WECampaignCallback = {
      onCampaignPrepared,
      onCampaignShown,
      onCampaignClicked,
      onCampaignException,
    };
    registerWECampaignCallback(WECampaignCallback);
  }, []);

  const onCampaignPrepared = (data) => {
    console.log('Arch: js:  App.js:  onCampaignPrepared: ', data);
  };

  const onCampaignShown = (data) => {
    console.log('Arch: js:  App.js:  onCampaignShown: ', data);
  }

  const onCampaignClicked = (data) => {
    console.log('Arch: js:  App.js:  onCampaignClicked: ', data);
  }

  const onCampaignException = (data) => {
    console.log('Arch: js:  App.js:  onCampaignException: ', data);
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
