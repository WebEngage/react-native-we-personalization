import React from 'react';
import {
  Text,
  SafeAreaView,
  StyleSheet,
  Pressable,
  TouchableHighlight,
  TextComponent,
  TextInput,
} from 'react-native';
import PropTypes from 'prop-types';
import WebEngage from 'react-native-webengage';
import {getValueFromAsyncStorage} from './Utils';

import {removeItem} from './Utils';
import { webengageInstance } from './Utils/WebEngageManager';
const ListScreen = (props) => {
  const {navigation} = props;
  
  const [isUserLoggedIn, setIsUserLoggedIn] = React.useState(false);

  const webengage = new WebEngage();
  const userNameRef = React.useRef(null);
  const [eventName, setEventName] = React.useState("")

  React.useEffect(() => {
    webengage.screen('test');
  });


  React.useEffect(() => {
    (async () => {
      const name = await getValueFromAsyncStorage('userName');
      if (name) {
        setIsUserLoggedIn(true);
      }
      userNameRef.current = name;
    })();
  });

  const updateEventName = (text) => {
    setEventName(text)
  }

  const trackEvent = () => {
    if(eventName) {
      webengageInstance.track(eventName);
    }

  }

  const logout = () => {
    removeItem('userName');
    navigation.navigate('login');
  };

  return (
    <SafeAreaView style={styles.mainContainer}>
      {isUserLoggedIn &&
      <TouchableHighlight
        style={[styles.button, styles.logout]}
        onPress={logout}
      >
        <Text> Logout </Text>
      </TouchableHighlight>
      }

      <Pressable
        style={styles.button}
        onPress={() => navigation.navigate('customScreens')}
      >
        <Text style={styles.textStyle}> Add Your Screens </Text>
      </Pressable>


      <TextInput
      value={eventName}
      style={styles.textBox}
      onChangeText={updateEventName}
      placeholder="Add Your Event"

      />
      <Pressable
        style={styles.trackButton}
        onPress={trackEvent}
      >
        <Text style={styles.textStyle}> Track Event </Text>
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
  trackButton: {
    marginBottom: 25,
    backgroundColor: '#ffffffe',
    borderWidth: 1,
    width: 150,
    height: 40,
    borderRadius: 10,
    justifyContent: 'center',
  },
  textBox: {
    fontSize: 18,
    borderBottomWidth: 2,
    height: 40,
    width: 350,
    marginVertical: 25
  },
  logout: {
    alignSelf: 'flex-end',
    width: 100,
    height: 35,
    alignItems: 'center',
    backgroundColor: '#f59518',
  },
  textStyle: {
    fontSize: 20,
    textAlign: 'center',
  },
});
export default ListScreen;

ListScreen.propTypes = {
  navigation: PropTypes.object,
};
