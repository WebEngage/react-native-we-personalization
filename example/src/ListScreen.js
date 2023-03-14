import { useFocusEffect } from '@react-navigation/native';
import React from 'react';
import {
  View,
  Button,
  Text,
  SafeAreaView,
  StyleSheet,
  Pressable,
  TouchableHighlight,
} from 'react-native';
import WebEngage from 'react-native-webengage';
import { removeItem } from './Utils';
const ListScreen = ({ navigation }) => {
  var webengage = new WebEngage();
  React.useEffect(() => {
    webengage.screen('test');

  });

  const logout = () => {
    removeItem('userName');
    navigation.navigate('login');
  };

  return (
    <SafeAreaView style={styles.mainContainer}>
      <TouchableHighlight
        style={[styles.button, styles.logout]}
        onPress={logout}
      >
        <Text> Logout </Text>
      </TouchableHighlight>
      <Pressable
        style={styles.button}
        onPress={() => navigation.navigate('regular')}
      >
        <Text style={styles.textStyle}> Regular </Text>
      </Pressable>

      <Pressable
        style={styles.button}
        onPress={() => navigation.navigate('scrollable')}
      >
        <Text style={styles.textStyle}> Scrollable </Text>
      </Pressable>

      <Pressable
        style={styles.button}
        onPress={() => navigation.navigate('flatlist')}
      >
        <Text style={styles.textStyle}> Flatlist </Text>
      </Pressable>

      <Pressable
        style={styles.button}
        onPress={() => navigation.navigate('customScreens')}
      >
        <Text style={styles.textStyle}> Custom Screens </Text>
      </Pressable>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  mainContainer: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  button: {
    marginBottom: 25,
    backgroundColor: '#91e058',
    borderWidth: 1,
    width: 250,
    height: 50,
    borderRadius: 30,
    justifyContent: 'center',
  },
  logout: {
    // justifyContent: 'flex-end'
    alignSelf: 'flex-end',
    width: 100,
    height: 35,
    alignItems: 'center',
    backgroundColor: '#f59518',
  },
  textStyle: {
    // color: 'white',
    fontSize: 20,
    textAlign: 'center',
  },
});
export default ListScreen;
