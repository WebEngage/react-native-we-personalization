import * as React from 'react';
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
  }, [userName]);

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
}
