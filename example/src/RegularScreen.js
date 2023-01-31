import * as React from 'react';

import { StyleSheet, View, Text, Dimensions, Button, AppState } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { WEPersonalization, initializePersonalization } from 'react-native-webengage-personalization';
import WebEngage from 'react-native-webengage';
const RegularScreen = ({ navigation }) => {
  var webengage = new WebEngage();

  useFocusEffect(
    React.useCallback(() => {
      webengage.screen('ET_home'); //
      console.log("ET_home is navigated")
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

  const onRendered_ak_test_1 = (d) => {
    console.log('onRendered_ak_test_1 triggered for ak_test_1', d);
  };

  const onPlaceholderException_aktest1 = (d) => {
    console.log('onPlaceholderException_aktest1 triggered for ak_test_1', d);
  };

  return (
    <View style={styles.container}>
      <WEPersonalization
        color="#32a852"
        style={styles.box}
        screenName="ET_home"
        propertyId="flutter_text" // ak_test_2 - custom
        personalizationCallback={personalizationCallback1}
        // onRendered={onRendered_}
      />
      <Button title={'Scroll screen'} onPress={navigateToScroll} />
      <Text>This text is from React Native</Text>
      <Text>But Above and below Views are from WebEngage </Text>
      {/* <WEPersonalization
        color="#12023f"
        style={styles.box2}
        propertyId="ak_test_1" // ak_test_1 - Text banner
        screenName="ET_home"
        personalizationCallback={personalizationCallback2} // onRendered
        onDataReceived={onRendered_ak_test_1}
        onPlaceholderException={onPlaceholderException_aktest1}
      /> */}
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
