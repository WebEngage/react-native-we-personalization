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

  const data1 = [];
  for (let i = 0; i < 100; i++) {
    data1.push({ id: i, title: `${i}th item` });
  }
  // const flatScreenName = 'scroll23';
  // const textProp = Platform.OS === 'android' ? 'banner_prop' : 432;
  // const bannerProp = Platform.OS === 'android' ? 'text_prop' : 532;

  // const flatScreenName = 'ET_home_2';
  // const textProp =
  //   Platform.OS === 'android' ? 'flutter_banner' : 99;
  // const bannerProp = Platform.OS === 'android' ? 'flutter_text' : 1002; // screen_home

  // Created this properties but not reflecting
  const flatScreenName = Platform.OS === 'android' ? 'react_screen' : 'screen1';
  const textProp = Platform.OS === 'android' ? 'react_text' : 21;
  const bannerProp = Platform.OS === 'android' ? 'react_banner' : 11; // screen_home

  // const flatScreenName = 'scroll';
  // const textProp = Platform.OS === 'android' ? 'text_prop' : 432;
  // const bannerProp = Platform.OS === 'android' ? 'banner_prop' : 532;

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

        {item.id === 2 ? (
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
        data={data1}
        initialNumToRender={10}
        // extraData={data}
        // bouncesZoom
        // overScrollMode="always"
        pagingEnabled
        renderItem={renderItem}
        removeClippedSubviews={true} //most imp => without this it(DidMoveToWindow - iOS) won't work
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
