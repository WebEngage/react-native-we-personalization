import * as React from 'react';

import LoginScreen from './LoginScreen';
import Navigation from './Navigation';
import { getValueFromAsyncStorage } from './Utils';
import { initWebEngage } from './Utils/WebEngageManager';
import { LogBox } from 'react-native';
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
      //enableDevMode();
    })();
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
