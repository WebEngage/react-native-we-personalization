import React from 'react';
import { TextInput, Text, Button, SafeAreaView } from 'react-native';
import WebEngage from 'react-native-webengage';
import { saveToAsyncStorage } from './Utils';

export default function LoginScreen(props) {
  const { isUserLoggedIn, updateLoginDetails } = props;
  const [userName, setuserName] = React.useState('');
  const webengage = new WebEngage();

  const onChange = (val) => {
    setuserName(val);
  };

  const login = () => {
    console.log('Logged IN  -> ', userName);
    webengage.user.login(userName);
    saveToAsyncStorage('userName', userName);
    updateLoginDetails(userName);
  };

  return (
    <SafeAreaView>
      <Text> Please enter your name to proceed </Text>
      <TextInput onChangeText={onChange} />
      <Button title={'login'} onPress={login} />
    </SafeAreaView>
  );
}
