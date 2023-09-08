import React from 'react';
import {View, Text, StyleSheet, TouchableOpacity} from 'react-native';
import {WEInlineWidget} from 'react-native-we-personalization';
import {webengageInstance} from '../Utils/WebEngageManager';
import {useIsFocused} from '@react-navigation/native';
import WEInlineWidgetTemplate from './WEInlineWidgetTemplate';

const MyAccount = () => {
  const isFocused = useIsFocused();

  React.useEffect(() => {
    if (isFocused) {
      // The screen is focused, you can track your screen here
      console.log('My Account screen is focused');
      webengageInstance.screen('account');
    }
  }, [isFocused]);

  const userInfo = {
    name: 'userName',
    email: 'userName@example.com',
    membershipLevel: 'Gold',
  };

  const handleDataReceived = data => {
    console.log('onDataReceived from Account');
    // Your onDataReceived logic here
  };

  const handlePlaceholderException = data => {
    console.log('onPlaceholderException from Account', data);
    // Your onPlaceholderException logic here
  };

  const handleRendered = data => {
    console.log('onRendered from Account');
    // Your onRendered logic here
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>My Account</Text>
      <View style={styles.userInfoContainer}>
        <Text style={styles.label}>Name:</Text>
        <Text style={styles.info}>{userInfo.name}</Text>
      </View>
      <View style={styles.userInfoContainer}>
        <Text style={styles.label}>Email:</Text>
        <Text style={styles.info}>{userInfo.email}</Text>
      </View>
      <View style={styles.userInfoContainer}>
        <Text style={styles.label}>Membership Level:</Text>
        <Text style={styles.info}>{userInfo.membershipLevel}</Text>
      </View>
      <TouchableOpacity style={styles.logoutButton}>
        <Text style={styles.logoutButtonText}>Logout</Text>
      </TouchableOpacity>
      <WEInlineWidgetTemplate
        screenName="account"
        androidPropertyId={'acc1_scr1'}
        iosPropertyId={75993}
        style={styles.weINlineStyle}
        onDataReceived={handleDataReceived}
        onPlaceholderException={handlePlaceholderException}
        onRendered={handleRendered}
      />
      <Text style={styles.welcomeText}> Welcome to Account Screen!</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
  },
  header: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  weINlineStyle: {
    height: 300,
    width: 400,
  },
  welcomeText: {
    fontSize: 24,
    fontWeight: 'bold',
    textAlign: 'center',
    marginTop: 20,
  },
  userInfoContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10,
  },
  label: {
    fontSize: 18,
    fontWeight: 'bold',
  },
  info: {
    fontSize: 18,
  },
  logoutButton: {
    marginTop: 20,
    backgroundColor: 'red',
    paddingVertical: 10,
    paddingHorizontal: 20,
    borderRadius: 5,
    alignItems: 'center',
  },
  logoutButtonText: {
    color: 'white',
    fontSize: 18,
    fontWeight: 'bold',
  },
});

export default MyAccount;
