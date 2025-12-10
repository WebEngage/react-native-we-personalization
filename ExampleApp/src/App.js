import * as React from 'react';

import LoginScreen from './LoginScreen';
import Navigation from './Navigation';
import { getValueFromAsyncStorage } from './Utils';
import { initWebEngage } from './Utils/WebEngageManager';
import { Alert, LogBox } from 'react-native';
import { initWePersonalization, registerWECampaignCallback, enableDevMode, deregisterWECampaignCallback } from 'react-native-we-personalization';
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
      // enableDevMode();
    })();
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

  React.useEffect(() => {
    const WECampaignCallback = {
      onCampaignPrepared,
      onCampaignShown,
      onCampaignClicked,
      onCampaignException,
    };
    console.log('Arch: js:  App.js: Registering campaign callbacks');

    registerWECampaignCallback(WECampaignCallback);
    return () => {
      console.log("Arch: deRegistering campaign callback")
      deregisterWECampaignCallback();
      // removeCustomViews();
    };
  }, [])

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
