import { useFocusEffect } from '@react-navigation/native';
import * as React from 'react';

import {
  StyleSheet,
  View,
  Dimensions,
  ScrollView,
  Image,
  Text,
  Button,
} from 'react-native';
import WebEngage from 'react-native-webengage';
import { WEPersonalization } from 'react-native-webengage-personalization';
const ScrollableScreen = ({ navigation }) => {
const webengage = new WebEngage();
  useFocusEffect(
    React.useCallback(() => {
      webengage.screen('ET_home'); //
      console.log("ET_home is navigated")
    }, [])
  );
  const personalizationCallback1 = (d) => {
    console.log('PPersonalization callback1 triggered for flutter_banner-', d);
  };

  const personalizationCallback2 = (d) => {
    console.log('Personalization callback2 triggered for flutter_text-', d);
  };

  const navigateToScroll = () => {
    navigation.navigate('flatlist');
  };

  return (
    <ScrollView style={styles.container}>
      <ScrollView style={styles.container} horizontal>
      <WEPersonalization
          style={styles.box}
          screenName="ET_home"
          propertyId="flutter_text"
          personalizationCallback={personalizationCallback1}
        />
        <Text style={styles.textStyle}> Second</Text>
        <Text style={styles.textStyle}> Second</Text>
        <Text style={styles.textStyle}> Second</Text>
        <Text style={styles.textStyle}> Second</Text>
        <Text style={styles.textStyle}> Second</Text>

        <Text style={styles.textStyle}> Second</Text>
        <Text style={styles.textStyle}> Second</Text>
      </ScrollView>
      <Text style={styles.nativeText}>
        Above View is from Native - Android (flutter_banner)
      </Text>

      <Button title={'Scroll screen'} onPress={navigateToScroll} />


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
        Below View is from Native - Android (flutter_text)
      </Text>
      <WEPersonalization
        style={styles.box2}
        screenName="ET_home"
        propertyId="flutter_banner"
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
  textStyle: { width: 150, marginRight: 50 },
  aboveBox: {
    width: '100%',
    height: 300,
    borderWidth: 1,
    padding: 10,
  },
  box: {
    width: Dimensions.get('window').width,
    height: 220,
    borderWidth: 10,
    borderColor: 'red',
    padding: 50,
  },
  box2: {
    width: Dimensions.get('window').width,
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
