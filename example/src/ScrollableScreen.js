import * as React from 'react';

import {
  StyleSheet,
  View,
  Dimensions,
  PixelRatio,
  ScrollView,
  Image,
  Text,
} from 'react-native';
import InView from 'react-native-component-inview';
import { WEPersonalization } from 'react-native-webengage-personalization';
const ScrollableScreen = ({ navigation }) => {
  const container = React.useRef(null);
  const container1 = React.useRef(null);
  const personalizationCallback1 = (d) => {
    console.log('PPersonalization callback1 triggered-', d);
  };

  const personalizationCallback2 = (d) => {
    console.log('PPersonalization callback2 triggered-', d);
  };

  const handleScroll = (event) => {
    console.log('### handleScroll -> ', event.nativeEvent);

    container1.current.onScroll(event);
  };

  const handleScroll1 = (event) => {
    container.current.onScroll(event);
  };

  var window = Dimensions.get('window');
  console.log('Dimensoin in  -> ', window);
  return (
    <ScrollView style={styles.container} onScroll={handleScroll}>
      <ScrollView style={styles.container} onScroll={handleScroll1} horizontal>
        <WEPersonalization
          color="#32a852"
          style={styles.box}
          propertyId="flutter_banner"
          screenName="ET_home"
          ref={container}
          // handleScroll={handleScroll}
          personalizationCallback={personalizationCallback1}
        />
        <Text style={{ width: 150, marginRight: 50,}}> Second</Text>
        <Text style={{ width: 150, marginRight: 50,}}> Second</Text>
        <Text style={{ width: 150, marginRight: 50,}}> Second</Text>
        <Text style={{ width: 150, marginRight: 50,}}> Second</Text>
        <Text style={{ width: 150, marginRight: 50,}}> Second</Text>
        <Text style={{ width: 150, marginRight: 50,}}> Second</Text>
        <Text style={{ width: 150, marginRight: 50,}}> Second</Text>
      </ScrollView>
      <Text style={styles.nativeText}>
        {' '}
        Above View is from Native - Android (flutter_banner){' '}
      </Text>

      <View style={styles.margin50} />
      <Image
        source={{ uri: 'https://picsum.photos/seed/picsum/200/300' }}
        style={styles.imageStyle}
        alt="Image Not loaded"
      />

      <Image
        source={{ uri: 'https://picsum.photos/id/237/200/300' }}
        style={styles.imageStyle}
        alt="Image Not loaded"
      />

      <Image
        source={{ uri: 'https://picsum.photos/200/300/?blur=2' }}
        style={styles.imageStyle}
        alt="Image Not loaded"
      />

      <Text style={styles.nativeText}>
        {' '}
        Below View is from Native - Android (flutter_text){' '}
      </Text>
      <WEPersonalization
        color="#12023f"
        style={styles.box2}
        propertyId="flutter_text"
        screenName="ET_home"
        ref={container1}
        personalizationCallback={personalizationCallback2}
      />
      <Text style={styles.nativeText}> Below View is React-Native</Text>
      <Image
        source={{ uri: 'https://picsum.photos/200/300/?blur=2' }}
        style={styles.imageStyle}
        alt="Image Not loaded"
      />
    </ScrollView>
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
  box: {
    // width: PixelRatio.getPixelSizeForLayoutSize(Dimensions.get('window').width),
    width: Dimensions.get('window').width,
    // height: PixelRatio.getPixelSizeForLayoutSize(80),
    height: 220,
    borderWidth: 10,
    borderColor: 'red',
    padding: 50,
  },
  box2: {
    width: Dimensions.get('window').width,

    // width: Dimensions.get('window').width,
    // height: PixelRatio.getPixelSizeForLayoutSize(150),
    height: 220,
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
export default ScrollableScreen;
