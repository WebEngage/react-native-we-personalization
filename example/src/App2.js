import * as React from 'react';
import {
  registerForCampaigns,
  unRegisterForCampaigns,
  userWillHandleDeepLink,
} from 'react-native-webengage-personalization';
import LoginScreen from './LoginScreen';
import Navigation from './Navigation';
import Flatter from './TestFlatList/Flatter';
import { getValueFromAsyncStorage } from './Utils';

export default function App() {
  return <Flatter />;
}
