import * as React from 'react';
import {
  registerForCampaigns,
  unRegisterForCampaigns,
  userWillHandleDeepLink,
} from 'react-native-webengage-personalization';
import { registerCustomPlaceHolder } from '../../src';
import { enableDevMode } from '../../src/MyLogs';
import LoginScreen from './LoginScreen';
import Navigation from './Navigation';
import { getValueFromAsyncStorage } from './Utils';
import { initWebEngage } from './Utils/WebEngageManager';
import WebEngage from 'react-native-webengage';


export default function App() {
  const [isUserLoggedIn, setIsUserLoggedIn] = React.useState(false);
  initWebEngage();
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


  const updateLoginDetails = (loginState) => {
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
