import * as React from 'react';

import {
  StyleSheet,
  View,
  Dimensions,
  PixelRatio,
  Text,
  SafeAreaView,
  FlatList,
  Platform,
  Button,
} from 'react-native';
import WebEngage from 'react-native-webengage';
import { WEInlineView } from 'react-native-webengage-personalization';
const FlatListScreen = ({ navigation }) => {
  const webengage = new WebEngage();

  React.useEffect(() => {
    webengage.screen(flatScreenName);
    console.log(flatScreenName + ' is navigated');

    return () => {
      console.log('@@@@ flatlist screen unmounted with empty depency');
    };
  }, []);
  const personalizationCallback1 = (d) => {
    console.log('PPersonalization callback1 triggered-', d);
  };

  const personalizationCallback2 = (d) => {
    console.log('PPersonalization callback2 triggered-', d);
  };

  const onRendered_1 = (d) => {
    console.log(
      'WER: Flatlist onRendered_1 triggered for -',
      d?.targetViewId,
      d
    );
  };

  const onDataReceived_1 = (d) => {
    console.log(
      'WER: Flatlist onDataReceived_1 triggered for ',
      d?.targetViewId,
      d
    );
  };

  const onPlaceholderException_1 = (d) => {
    console.log(
      'WER: Flatlist onPlaceholderException_1 triggered for ',
      d?.targetViewId,
      d
    );
  };

  const onRendered_2 = (d) => {
    console.log(
      'WER: Flatlist onRendered_2 callback triggered for ',
      d?.targetViewId,
      d
    );
  };

  const onDataReceived_2 = (d) => {
    console.log(
      'WER: Flatlist onDataReceived_2 triggered for ',
      d?.targetViewId,
      d
    );
  };

  const onPlaceholderException_2 = (d) => {
    console.log(
      'WER: Flatlist onPlaceholderException_2 triggered for ',
      d?.targetViewId,
      d
    );
  };

  const data = [
    { id: 0, title: 'zeroth item' },
    { id: 1, title: 'First item' },
    { id: 2, title: 'Second item' },
    { id: 3, title: 'Third item' },
    { id: 4, title: 'Fourth item' },
    { id: 5, title: 'Fifth item' },
    { id: 6, title: 'Sixth item' },
    { id: 7, title: 'Seventh item' },
    { id: 8, title: 'Eighth item' },
    { id: 9, title: 'Ninth item' },
    { id: 10, title: 'Tenth item' },
    { id: 11, title: 'Eleventh item' },
    { id: 12, title: 'Twelves item' },
    { id: 13, title: 'Thirteenth item' },
  ];
  // const flatScreenName = 'scroll23';
  // const textProp = Platform.OS === 'android' ? 'banner_prop' : 432;
  // const bannerProp = Platform.OS === 'android' ? 'text_prop' : 532;

  // const flatScreenName = 'ET_home_2';
  // const textProp =
  //   Platform.OS === 'android' ? 'flutter_banner' : 99;
  // const bannerProp = Platform.OS === 'android' ? 'flutter_text' : 1002; // screen_home


  // Created this properties but not reflecting
  const flatScreenName = 'react_screen';
  const textProp =
    Platform.OS === 'android' ? 'react_text' : 888;
  const bannerProp = Platform.OS === 'android' ? 'react_banner' : 777; // screen_home


  const navigateToRegular = () => {
    navigation.navigate('regular');
  };

  const navigateToScroll = () => {
    navigation.navigate('scrollable');
  };

  const renderItem = ({ item }) => {
    return (
      <View style={styles.itemView}>
        <Text> {item.title} </Text>
        {item.id === 1 ? (
          <WEInlineView
            style={styles.box}
            propertyId={bannerProp}
            screenName={flatScreenName}
            // personalizationCallback={personalizationCallback1}
            onRendered={onRendered_1}
            onDataReceived={onDataReceived_1}
            onPlaceholderException={onPlaceholderException_1}
          />
        ) : null}
        <Button title={'Regular screen'} onPress={navigateToRegular} />
        <View style={styles.margin50} />
        <Button title={'Scroll screen'} onPress={navigateToScroll} />

        {item.id === 4 ? (
          <WEInlineView
            style={styles.box2}
            propertyId={textProp}
            screenName={flatScreenName}
            onRendered={onRendered_2}
            onDataReceived={onDataReceived_2}
            onPlaceholderException={onPlaceholderException_2}
            // personalizationCallback={personalizationCallback2}
          />
        ) : null}
      </View>
    );
  };

  return (
    <SafeAreaView>
      <FlatList
        keyExtractor={(item) => item.id}
        initialNumToRender={5}
        data={data}
        extraData={data}
        renderItem={renderItem}
      />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  margin50: {
    marginTop: 50,
  },
  nativeText: {
    backgroundColor: '#dde691',
    fontSize: 18,
  },
  aboveBox: {
    width: '100%',
    height: 300,
    borderWidth: 1,
    padding: 10,
  },
  itemView: {
    height: 400,
    borderWidth: 2,
    marginBottom: 30,
  },
  box: {
    // width: PixelRatio.getPixelSizeForLayoutSize(Dimensions.get('window').width),
    width: Dimensions.get('window').width,
    height: PixelRatio.getPixelSizeForLayoutSize(80),
    padding: 50,
  },
  box2: {
    // width: PixelRatio.getPixelSizeForLayoutSize(Dimensions.get('window').width),
    width: Dimensions.get('window').width,
    height: PixelRatio.getPixelSizeForLayoutSize(100),
    padding: 50,
  },
  imageStyle: {
    height: 500,
    width: Dimensions.get('window').width,
    resizeMode: 'stretch',
    marginBottom: 20,
  },
});
export default FlatListScreen;
