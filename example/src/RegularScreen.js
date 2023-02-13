import * as React from 'react';

import {
  StyleSheet,
  View,
  Text,
  Dimensions,
  Button,
  AppState,
  ScrollView,
  Platform,
} from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import {
  WEPersonalization,
  initializePersonalization,
} from 'react-native-webengage-personalization';
import WebEngage from 'react-native-webengage';
const RegularScreen = ({ navigation }) => {
  var webengage = new WebEngage();

  useFocusEffect(
    React.useCallback(() => {
      webengage.screen(regularScreenName);
      console.log(regularScreenName + 'navigted');
      // WEPersonalization.registerWEPlaceholderCallback("ak_test_2", callback);
      return () => {
        // Perform cleanup on blur
      };
    }, [])
  );

  const onRendered_1 = (d) => {
    console.log(
      'WER: Regular onRendered_1 triggered for -',
      d?.targetViewId,
      d
    );
  };

  const onDataReceived_1 = (d) => {
    console.log(
      'WER: Regular onDataReceived_1 triggered for ',
      d?.targetViewId,
      d
    );
  };

  const onPlaceholderException_1 = (d) => {
    console.log(
      'WER: Regular onPlaceholderException_1 triggered for ',
      d?.targetViewId,
      d
    );
  };

  const onRendered_2 = (d) => {
    console.log(
      'WER: Regular onRendered_2 callback triggered for ',
      d?.targetViewId,
      d
    );
  };

  const onDataReceived_2 = (d) => {
    console.log(
      'WER: Regular onDataReceived_2 triggered for ',
      d?.targetViewId,
      d
    );
  };

  const onPlaceholderException_2 = (d) => {
    console.log(
      'WER: Regular onPlaceholderException_2 triggered for ',
      d?.targetViewId,
      d
    );
  };

  const navigateToScroll = () => {
    navigation.navigate('scrollable');
  };

  // const screen1Properties = Platform.OS === 'android' ? 12 : 12; // screen1
  const regularScreenName = 'ET_home';
  const screenHomeProperties =
    Platform.OS === 'android' ? 'flutter_banner' : 99;
  const screenProperties = Platform.OS === 'android' ? 'flutter_text' : 1002; // screen_home

  return (
    <View style={styles.container}>
      <WEPersonalization
        style={styles.box}
        screenName={regularScreenName}
        propertyId={screenHomeProperties} // ak_test_2 - custom
        color="#32a852"
        onRendered={onRendered_1}
        onDataReceived={onDataReceived_1}
        onPlaceholderException={onPlaceholderException_1}
      />
      <Button title={'Scroll screen'} onPress={navigateToScroll} />
      <Text>This text is from React Native</Text>
      <Text>But Above and below Views are from WebEngage </Text>
      <WEPersonalization
        color="#12023f"
        style={styles.box2}
        propertyId={screenProperties}
        screenName={regularScreenName}
        onRendered={onRendered_2} // onRendered
        onDataReceived={onDataReceived_2}
        onPlaceholderException={onPlaceholderException_2}
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
    // borderWidth: 10,
    // borderColor: 'red',
    // padding: 50,
  },
  box2: {
    width: Dimensions.get('window').width,
    height: 240,
  },
});
export default RegularScreen;
