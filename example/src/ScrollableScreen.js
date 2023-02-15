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
  Platform,
} from 'react-native';
import WebEngage from 'react-native-webengage';
import { WEInlineView,
  registerCustomPlaceHolder,
  unRegisterCustomPlaceHolder
   } from 'react-native-webengage-personalization';
const ScrollableScreen = ({ navigation }) => {
  const webengage = new WebEngage();
  useFocusEffect(
    React.useCallback(() => {
      webengage.screen(scrollScreen);
      console.log(scrollScreen + ' is navigated');
    }, [])
  );

  React.useEffect(() => {
    const propertyId = 'banner_prop';
    registerCustomPlaceHolder(
      propertyId,
      scrollScreen,
      custom_onDataReceived,
      custom_onPlaceholderException
    );

    return () => {
      unRegisterCustomPlaceHolder(propertyId, scrollScreen);
    };
  });

  const custom_onDataReceived = (d) => {
    console.log('WER: Custom onDataReceived for-', d?.targetViewId, d);
  };

  const custom_onPlaceholderException = (d) => {
    console.log('WER: Custom onPlaceholderException2 for ', d?.targetViewId, d);
  };
  const personalizationCallback1 = (d) => {
    console.log('PPersonalization callback1 triggered for flutter_banner-', d);
  };

  const personalizationCallback2 = (d) => {
    console.log('Personalization callback2 triggered for flutter_text-', d);
  };

  const navigateToRegular = () => {
    navigation.navigate('regular');
  };

  const navigateToFlatList = () => {
    navigation.navigate('flatlist');
  };

  const onRendered_1 = (d) => {
    console.log('WER: Scrollable onRendered_1 triggered for -', d?.targetViewId, d);
  };

  const onDataReceived_1 = (d) => {
    console.log('WER: Scrollable onDataReceived_1 triggered for ', d?.targetViewId, d);
  };

  const onPlaceholderException_1 = (d) => {
    console.log('WER: Scrollable onPlaceholderException_1 triggered for ', d?.targetViewId, d);
  };

  const onRendered_2 = (d) => {
    console.log('WER: Scrollable onRendered_2 callback triggered for ', d?.targetViewId, d);
  };

  const onDataReceived_2 = (d) => {
    console.log('WER: Scrollable onDataReceived_2 triggered for ', d?.targetViewId, d);
  };

  const onPlaceholderException_2 = (d) => {
    console.log('WER: Scrollable onPlaceholderException_2 triggered for ', d?.targetViewId, d);
  };

  const scrollScreen = 'scroll';
  const textProp = Platform.OS === 'android' ? 'text_prop' : 432;
  const bannerProp = Platform.OS === 'android' ? 'banner_prop' : 532;

  return (
    <ScrollView style={styles.container}>
      <ScrollView style={styles.container} horizontal>
        <WEInlineView
          style={styles.box}
          screenName={scrollScreen}
          propertyId={textProp}
          onRendered={onRendered_1}
          onDataReceived={onDataReceived_1}
          onPlaceholderException={onPlaceholderException_1}
        />
        <Text style={styles.textStyle}> First</Text>
        <Text style={styles.textStyle}> Second</Text>
        <Text style={styles.textStyle}> Third</Text>
        <Text style={styles.textStyle}> Four</Text>
        <Text style={styles.textStyle}> Five</Text>

        <Text style={styles.textStyle}> Six</Text>
        <Text style={styles.textStyle}> Seven</Text>
      </ScrollView>
      <Text style={styles.nativeText}>
        Above View is from Native - Android (flutter_banner)
      </Text>

      <Button title={'FlatList screen'} onPress={navigateToFlatList} />
      <View style={styles.margin50} />

      <Button title={'Regular screen'} onPress={navigateToRegular} />

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
      {/* <WEPersonalization
        style={styles.box2}
        screenName={scrollScreen}
        propertyId={bannerProp}
        onRendered={onRendered_2}
        onDataReceived={onDataReceived_2}
        onPlaceholderException={onPlaceholderException_2}
        // personalizationCallback={personalizationCallback2}
      /> */}
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
