import * as React from 'react';
import {
  registerForCampaigns,
  unRegisterForCampaigns,
  userWillHandleDeepLink,
} from 'react-native-webengage-personalization';
import LoginScreen from './LoginScreen';
import Navigation from './Navigation';
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
      onCampaignException,
    };
    const doesUserHandelCallbacks = true;
    registerForCampaigns(callbacks);
    userWillHandleDeepLink(doesUserHandelCallbacks);
    return () => {
      unRegisterForCampaigns();
    };
  }, [userName]);

  const onCampaignClicked = (data) => {
    console.log('App: onCampaignClicked ', data);
  };

  const onCampaignPrepared = (data) => {
    console.log('App: onCampaignPrepared ', data);
  };

  const onCampaignShown = (data) => {
    console.log('App: onCampaignShown ', data);
  };

  const onCampaignException = (data) => {
    console.log('App: onCampaignException ', data);
  };

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
}
