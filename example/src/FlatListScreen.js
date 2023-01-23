import * as React from 'react';

import {
  StyleSheet,
  View,
  Dimensions,
  PixelRatio,
  ScrollView,
  Image,
  Text,
  SafeAreaView,
  FlatList,
} from 'react-native';
import { WEPersonalization } from 'react-native-webengage-personalization';
const FlatListScreen = ({ navigation }) => {
  var window = Dimensions.get('window');

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

  const onViewableItemsChanged = ({ viewableItems }) => {
    console.log('viewableItemsss --> ', viewableItems);
  };
  const viewabilityConfig = {
    waitForInteraction: false,
    viewAreaCoveragePercentThreshold: 10,
  };

  const renderItem = ({ item }) => {
    return (
      <View style={styles.itemView}>
        <Text> {item.title} </Text>
        {item.id === 2 ? (
          <WEPersonalization
            color="#32a852"
            style={styles.box}
            propertyId="flutter_banner"
            screenName="ET_home"
            isVisibleInTheViewport={true}
            personalizationCallback={personalizationCallback1}
          />
        ) : null}
        {item.id === 7 ? (
          <WEPersonalization
            color="#12023f"
            style={styles.box2}
            propertyId="flutter_text"
            screenName="ET_home"
            personalizationCallback={personalizationCallback2}
          />
        ) : null}
      </View>
    );
  };
  const handleScroll = (event) => {
    console.log(' FlatList handleScroll -> ', event.nativeEvent);

  };

  return (
    <SafeAreaView>
      <FlatList
        data={data}
        onScroll={handleScroll}
        renderItem={renderItem}
        viewabilityConfig={viewabilityConfig}
        onViewableItemsChanged={onViewableItemsChanged}
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
    borderWidth: 10,
    borderColor: 'red',
    padding: 50,
  },
  box2: {
    // width: PixelRatio.getPixelSizeForLayoutSize(Dimensions.get('window').width),
    width: Dimensions.get('window').width,
    height: PixelRatio.getPixelSizeForLayoutSize(150),
    borderWidth: 10,
    borderColor: 'red',
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
