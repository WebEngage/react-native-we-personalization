import React from 'react';
import {
  Text,
  SafeAreaView,
  StyleSheet,
  Pressable,
  TouchableHighlight,
} from 'react-native';
import PropTypes from 'prop-types';
import WebEngage from 'react-native-webengage';
import {getValueFromAsyncStorage} from './Utils';

import {removeItem} from './Utils';
const ListScreen = (props) => {
  const {navigation} = props;
  const [isUserLoggedIn, setIsUserLoggedIn] = React.useState(false);

  const webengage = new WebEngage();
  const userNameRef = React.useRef(null);

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
