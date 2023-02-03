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
} from 'react-native';
import WebEngage from 'react-native-webengage';
import { WEPersonalization } from 'react-native-webengage-personalization';
const FlatListScreen = () => {
  const webengage = new WebEngage();

  React.useEffect(() => {
    webengage.screen(flatScreenName);
    console.log(flatScreenName + ' is navigated');
  }, []);

  const personalizationCallback1 = (d) => {
    console.log('PPersonalization callback1 triggered-', d);
  };

  const personalizationCallback2 = (d) => {
    console.log('PPersonalization callback2 triggered-', d);
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
  const flatScreenName = 'scroll23';
  const textProp = Platform.OS === 'android' ? 'banner_prop' : 432;
  const bannerProp = Platform.OS === 'android' ? 'text_prop' : 532;

  const renderItem = ({ item }) => {
    return (
      <View style={styles.itemView}>
        <Text> {item.title} </Text>
        {item.id === 1 ? (
          <WEPersonalization
            style={styles.box}
            propertyId={bannerProp}
            screenName={flatScreenName}
            personalizationCallback={personalizationCallback1}
          />
        ) : null}
        {item.id === 4 ? (
          <WEPersonalization
            style={styles.box2}
            propertyId={textProp}
            screenName={flatScreenName}
            personalizationCallback={personalizationCallback2}
          />
        ) : null}
      </View>
    );
  };

  return (
    <SafeAreaView>
      <FlatList data={data} renderItem={renderItem} />
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
