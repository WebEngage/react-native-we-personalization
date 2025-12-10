import React from 'react';
import {
  Text,
  SafeAreaView,
  StyleSheet,
  Pressable,
  TouchableHighlight,
  View,
} from 'react-native';
import WebEngage from 'react-native-webengage';
import {getValueFromAsyncStorage, removeItem} from './Utils';
import {getArchitectureInfo} from './Utils/ArchitectureHelper';
import {SCREEN_NAMES, BUTTON_LABELS} from './constants';

interface ArchInfo {
  architectureMode: string;
  bridgeMode: string;
  isTurboModuleEnabled: boolean;
  isFabricEnabled: boolean;
}

interface ListScreenProps {
  navigation: any;
}

const ListScreen: React.FC<ListScreenProps> = ({navigation}) => {
  const [isUserLoggedIn, setIsUserLoggedIn] = React.useState(false);
  const [archInfo, setArchInfo] = React.useState<ArchInfo | null>(null);

  const webengage = new WebEngage();
  const userNameRef = React.useRef<string | null>(null);

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
      {isUserLoggedIn && (
        <TouchableHighlight
          style={[styles.button, styles.logout]}
          onPress={logout}
        >
          <Text> Logout </Text>
        </TouchableHighlight>
      )}

      {archInfo && (
        <View style={styles.archInfoContainer}>
          <Text style={styles.archTitle}>Architecture Info</Text>
          <Text style={styles.archText}>Mode: {archInfo.architectureMode}</Text>
          <Text style={styles.archText}>Bridge: {archInfo.bridgeMode}</Text>
          <Text style={styles.archText}>
            TurboModules: {archInfo.isTurboModuleEnabled ? 'Enabled' : 'Disabled'}
          </Text>
          <Text style={styles.archText}>
            Fabric: {archInfo.isFabricEnabled ? 'Enabled' : 'Disabled'}
          </Text>
        </View>
      )}

      <Pressable
        style={styles.button}
        onPress={() => navigation.navigate('customScreens')}
      >
        <Text style={styles.textStyle}> Add Your custom Screens </Text>
      </Pressable>

      <Pressable
        style={[styles.button, styles.homeButton]}
        onPress={() => navigation.navigate(SCREEN_NAMES.HOME)}
      >
        <Text style={styles.textStyle}>{BUTTON_LABELS.HOME}</Text>
      </Pressable>

      <Pressable
        style={[styles.button, styles.ordersButton]}
        onPress={() => navigation.navigate(SCREEN_NAMES.ORDERS)}
      >
        <Text style={styles.textStyle}>{BUTTON_LABELS.ORDERS}</Text>
      </Pressable>

      <Pressable
        style={[styles.button, styles.cartButton]}
        onPress={() => navigation.navigate(SCREEN_NAMES.CART)}
      >
        <Text style={styles.textStyle}>{BUTTON_LABELS.CART}</Text>
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
  homeButton: {
    backgroundColor: '#4CAF50',
  },
  ordersButton: {
    backgroundColor: '#673AB7',
  },
  cartButton: {
    backgroundColor: '#FF5722',
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
