import React from 'react';
import { TextInput, Text, Button, SafeAreaView } from 'react-native';
import WebEngage from 'react-native-webengage';
import { saveToAsyncStorage } from './Utils';
import { webengageInstance } from './Utils/WebEngageManager';

export default function LoginScreen(props) {
  const { isUserLoggedIn, updateLoginDetails } = props;
  const [userName, setuserName] = React.useState('');

  const onChange = (val) => {
    setuserName(val);
  };

  const login = () => {
    console.log('Logged IN  -> ', userName);

    saveToAsyncStorage('userName', userName);
    updateLoginDetails(userName);
    webengageInstance.user.login(userName);
  };

  return (
    <SafeAreaView>
      <Text> Please enter your name to proceed </Text>
      <TextInput onChangeText={onChange} />
      <Button title={'login'} onPress={login} />
    </SafeAreaView>
  );
}
