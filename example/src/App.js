import * as React from 'react';
import WebEngage from 'react-native-webengage';
import Navigation from './Navigation';

export default function App() {
  var webengage = new WebEngage();
  webengage.user.login('Ak112');
  return <Navigation />;
}
