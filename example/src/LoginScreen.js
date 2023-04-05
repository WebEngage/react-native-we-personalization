import { useNavigation } from '@react-navigation/native';
import React from 'react';
import { StyleSheet, TouchableHighlight } from 'react-native';
import { TextInput, Text, Button, SafeAreaView } from 'react-native';
import { saveToAsyncStorage } from './Utils';
import { webengageInstance } from './Utils/WebEngageManager';

export default function LoginScreen(props) {
  const {
    updateLoginDetails = () => {},
    navigation = null,
    updateGuestState = () => {},
  } = props;
  const [userName, setuserName] = React.useState('');

  const onChange = (val) => {
    setuserName(val);
  };

  const login = () => {
    if (userName.length) {
      saveToAsyncStorage('userName', userName);
      if (updateLoginDetails) {
        updateLoginDetails(userName);
      }
      if (navigation) {
        navigation.navigate('main');
      }
      webengageInstance.user.login(userName);
    }
  };
  const skipLogin = () => {
    updateGuestState()
  }

  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.textDesc}> Please enter your name to proceed </Text>
      <TextInput onChangeText={onChange} style={styles.textBox} />
      <TouchableHighlight onPress={login} style={styles.button}>
        <Text style={styles.btnText}> Login </Text>
      </TouchableHighlight>
      <TouchableHighlight onPress={skipLogin} style={styles.button}>
        <Text style={styles.btnText}> Skip Login </Text>
      </TouchableHighlight>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    justifyContent: 'center',
    alignItems: 'center',
  },
  textBox: {
    borderBottomWidth: 1,
    width: 200,
    height: 50,
    marginVertical: 20,
    color: '#000'
  },
  textDesc: {
    fontSize: 18,
    color: '#000',
    fontWeight: 'bold',
    marginVertical: 20,
  },
  button: {
    marginTop: 25,
    backgroundColor: '#000',
    borderWidth: 1,
    width: 100,
    height: 50,
    borderRadius: 15,
    justifyContent: 'center',
    alignItems: 'center',
  },
  btnText: {
    color: '#fff',
    fontSize: 18,
    fontWeight: 'bold',
  },
});
