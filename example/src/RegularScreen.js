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
  WEInlineView,
  registerCustomPlaceHolder,
  unRegisterCustomPlaceHolder,
} from 'react-native-webengage-personalization';
import WebEngage from 'react-native-webengage';
const RegularScreen = ({ navigation }) => {
  var webengage = new WebEngage();

  useFocusEffect(
    React.useCallback(() => {
      webengage.screen(regularScreenName);
      return () => {
      };
    }, [])
  );

  const custom_onRendered = (d) => {
    console.log('Example: Custom onDataReceived for-', d?.targetViewId, d);
  };

  const custom_onPlaceholderException = (d) => {
    console.log('Example: Custom onPlaceholderException  for ', d?.targetViewId, d);
  };

  const onRendered_1 = (d) => {
    console.log(
      'Example: Regular onRendered_1 triggered for -',
      d?.targetViewId,
      d
    );
  };

  const onDataReceived_1 = (d) => {
    console.log(
      'Example: Regular onDataReceived_1 triggered for ',
      d?.targetViewId,
      d
    );
  };

  const onPlaceholderException_1 = (d) => {
    console.log(
      'Example: Regular onPlaceholderException_1 triggered for ',
      d?.targetViewId,
      d
    );
  };

  const onRendered_2 = (d) => {
    console.log(
      'Example: Regular onRendered_2 callback triggered for ',
      d?.targetViewId,
      d
    );
  };

  const onDataReceived_2 = (d) => {
    console.log(
      'Example: Regular onDataReceived_2 triggered for ',
      d?.targetViewId,
      d
    );
  };

  const onPlaceholderException_2 = (d) => {
    console.log(
      'Example: Regular onPlaceholderException_2 triggered for ',
      d?.targetViewId,
      d
    );
  };

  const navigateToScroll = () => {
    navigation.navigate('scrollable');
  };

  const navigateToFlatList = () => {
    navigation.navigate('flatlist');
  };

  const regularScreenName = Platform.OS === 'android' ? 'ET_home' : 'ET_home';
  const screenHomeProperties = Platform.OS === 'android' ? 'ninetyNine' : 99;
  const screenProperties = Platform.OS === 'android' ? 'flutter_text' : 1002; // screen_home

  // screendark(screen) | dm2(prop) - android (Working)
  // registerForCampaigns(clickCb, shownCb)
  // add for custom callback

  return (
    <View style={styles.container}>
      <WEInlineView
        style={styles.box}
        screenName={regularScreenName}
        propertyId={screenHomeProperties}
        color="#32a852"
        onRendered={onRendered_1}
        onDataReceived={onDataReceived_1}
        onPlaceholderException={onPlaceholderException_1}
      />
      <Button title={'Scroll screen'} onPress={navigateToScroll} />
      <View style={styles.margin50} />
      <Button title={'Flatlist screen'} onPress={navigateToFlatList} />
      <Text>This text is from React Native</Text>
      <Text>But Above and below Views are from WebEngage </Text>
      {/* <WEInlineView
        color="#12023f"
        style={styles.box2}
        propertyId={screenProperties}
        screenName={regularScreenName}
        onRendered={onRendered_2} // onRendered
        onDataReceived={onDataReceived_2}
        onPlaceholderException={onPlaceholderException_2}
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
