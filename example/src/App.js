import * as React from 'react';
import WebEngagePlugin from 'react-native-webengage';
import { registerCustomPlaceHolder, registerForCampaigns, unRegisterForCampaigns } from '../../src';
import LoginScreen from './LoginScreen';
import Navigation from './Navigation';
import RegularScreen from './RegularScreen';
import ScrollableScreen from './ScrollableScreen';
import { getValueFromAsyncStorage } from './Utils';

export default function App() {
  const [isUserLoggedIn, setIsUserLoggedIn] = React.useState(false);
  const userName = getValueFromAsyncStorage('userName');

  React.useEffect(() => {
    if (userName) {
      setIsUserLoggedIn(true);
    }
  const callbacks = {
    onCampaignPrepared,
    onCampaignShown,
    onCampaignClicked,
    onCampaignException
  }
  registerForCampaigns(callbacks)
  return () => {
    // unre
    unRegisterForCampaigns();
  }
  }, [userName]);


  const onCampaignClicked = (data) => {
    console.log("App: onCampaignClicked ",data)
  }

  const onCampaignPrepared = (data) => {
    console.log("App: onCampaignPrepared ",data)
  }

  const onCampaignShown = (data) => {
    console.log("App: onCampaignShown ",data)
  }

  const onCampaignException = (data) => {
    console.log("App: onCampaignException ",data)
  }

  const updateLoginDetails = (loginState) => {
    console.log('user login state updated', loginState);
    setIsUserLoggedIn(loginState);
  };
  if (isUserLoggedIn) {
    return <Navigation />;
  } else {
    return (
      <LoginScreen isUserLoggedIn updateLoginDetails={updateLoginDetails} />
    );
  }

  // return <RegularScreen />;
  // return <ScrollableScreen />;
}
