import * as React from 'react';

import { StyleSheet, View, Text, Dimensions, Button } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { WEPersonalization } from 'react-native-webengage-personalization';
import WebEngage from 'react-native-webengage';
const RegularScreen = ({ navigation }) => {
  var webengage = new WebEngage();

  useFocusEffect(
    React.useCallback(() => {
      webengage.screen('ak_test');
      // WEPersonalization.registerWEPlaceholderCallback("ak_test_2", callback);
      return () => {
        // Perform cleanup on blur
      };
    }, [])
  );

  React.useEffect(() => {
    return () => {
      console.log('Unmouting from regular screen');
    };
  }, []);

  const personalizationCallback1 = (d) => {
    console.log(
      'PPersonalization callback1 triggered for ak_test_2 (Custom) -',
      d
    );
  };

  const personalizationCallback2 = (d) => {
    console.log('PPersonalization callback2 triggered for ak_test_1', d);
  };

  const navigateToScroll = () => {
    navigation.navigate('scrollable');
  };

  return (
    <View style={styles.container}>
      <WEPersonalization
        color="#32a852"
        style={styles.box}
        propertyId="ak_test_2" // ak_test_2 - custom
        screenName="ak_test"
        personalizationCallback={personalizationCallback1}
      />
      <Button title={'Scroll screen'} onPress={navigateToScroll} />
      <Text>This text is from React Native</Text>
      <Text>But Above and below Views are from WebEngage </Text>
      <WEPersonalization
        color="#12023f"
        style={styles.box2}
        propertyId="ak_test_1" // ak_test_1 - Text banner
        screenName="ak_test"
        personalizationCallback={personalizationCallback2} //onDataReceived
      />
      <View style={styles.margin20} />

      <View style={styles.margin20} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  margin20: {
    marginTop: 20,
  },
  aboveBox: {
    width: '100%',
    height: 300,
    borderWidth: 1,
    padding: 10,
  },
  box: {
    width: Dimensions.get('window').width,
    height: 200,
    borderWidth: 10,
    borderColor: 'red',
    padding: 50,
  },
  box2: {
    width: Dimensions.get('window').width,
    height: 240,
  },
});
export default RegularScreen;
