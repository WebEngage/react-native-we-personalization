import * as React from 'react';
import LoginScreen from './LoginScreen';
import Navigation from './Navigation';
import {getValueFromAsyncStorage} from './Utils';
import {initWebEngage} from './Utils/WebEngageManager';
import {LogBox} from 'react-native';
import {
  initWePersonalization,
  registerWECampaignCallback,
  deregisterWECampaignCallback,
} from 'react-native-we-personalization';

// LogBox.ignoreAllLogs();

export default function App() {
  const [isUserLoggedIn, setIsUserLoggedIn] = React.useState(false);
  const [continueAsGuest, setContinueAsGuest] = React.useState(false);

  initWebEngage();
  initWePersonalization();
  const userNameRef = React.useRef<string | null>(null);

  React.useEffect(() => {
    (async () => {
      const name = await getValueFromAsyncStorage('userName');
      if (name) {
        setIsUserLoggedIn(true);
      }
      userNameRef.current = name;
    })();
  }, []);

  const onCampaignPrepared = (data: any) => {
    console.log('App: onCampaignPrepared:', data);
  };

  const onCampaignShown = (data: any) => {
    console.log('App: onCampaignShown:', data);
  };

  const onCampaignClicked = (data: any) => {
    console.log('App: onCampaignClicked:', data);
  };

  const onCampaignException = (data: any) => {
    console.log('App: onCampaignException:', data);
  };

  React.useEffect(() => {
    const WECampaignCallback = {
      onCampaignPrepared,
      onCampaignShown,
      onCampaignClicked,
      onCampaignException,
    };
    console.log('App: Registering campaign callbacks');

    registerWECampaignCallback(WECampaignCallback);
    return () => {
      console.log('App: Deregistering campaign callback');
      deregisterWECampaignCallback();
    };
  }, []);

  const updateLoginDetails = (loginState: string) => {
    setIsUserLoggedIn(!!loginState);
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
          updateLoginDetails={updateLoginDetails}
          updateGuestState={updateGuestState}
        />
      );
    } else {
      return <Navigation />;
    }
  }
}
