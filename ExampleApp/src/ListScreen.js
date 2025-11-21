import React from 'react';
import {
  Text,
  SafeAreaView,
  StyleSheet,
  Pressable,
  TouchableHighlight,
  View,
} from 'react-native';
import PropTypes from 'prop-types';
import WebEngage from 'react-native-webengage';
import {getValueFromAsyncStorage} from './Utils';
import {getArchitectureInfo} from './Utils/ArchitectureHelper';

import {removeItem} from './Utils';
const ListScreen = (props) => {
  const {navigation} = props;
  const [isUserLoggedIn, setIsUserLoggedIn] = React.useState(false);
  const [archInfo, setArchInfo] = React.useState(null);

  const webengage = new WebEngage();
  const userNameRef = React.useRef(null);

  React.useEffect(() => {
    webengage.screen('test');
    setArchInfo(getArchitectureInfo());
  }, []);


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

      {archInfo && (
        <View style={styles.archInfoContainer}>
          <Text style={styles.archTitle}>Architecture Info</Text>
          <Text style={styles.archText}>Mode: {archInfo.architectureMode}</Text>
          <Text style={styles.archText}>Bridge: {archInfo.bridgeMode}</Text>
          <Text style={styles.archText}>TurboModules: {archInfo.isTurboModuleEnabled ? 'Enabled' : 'Disabled'}</Text>
          <Text style={styles.archText}>Fabric: {archInfo.isFabricEnabled ? 'Enabled' : 'Disabled'}</Text>
        </View>
      )}

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
  archInfoContainer: {
    backgroundColor: '#e3f2fd',
    padding: 15,
    borderRadius: 10,
    marginBottom: 20,
    width: 250,
    borderWidth: 1,
    borderColor: '#2196f3',
  },
  archTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 10,
    textAlign: 'center',
    color: '#1976d2',
  },
  archText: {
    fontSize: 14,
    marginBottom: 5,
    color: '#333',
  },
});
export default ListScreen;

ListScreen.propTypes = {
  navigation: PropTypes.object,
};
