import React from 'react';
import {View, Text, StyleSheet} from 'react-native';
import {WEInlineWidget} from 'react-native-we-personalization';
import {webengageInstance} from '../Utils/WebEngageManager';
import {useIsFocused} from '@react-navigation/native';

const Offers = () => {
  React.useEffect(() => {
    webengageInstance.track('offer');
  });
  const isFocused = useIsFocused();

  React.useEffect(() => {
    if (isFocused) {
      // The screen is focused, you can perform actions here
      console.log('Offer screen is focused');
      webengageInstance.screen('offer');
    } else {
      // The screen is not focused
      console.log('Offer screen is not focused');
    }
  }, [isFocused]);

  const handleDataReceived = data => {
    console.log('onDataReceived from Offers');
    // Your onDataReceived logic here
  };

  const handlePlaceholderException = data => {
    console.log('onPlaceholderException from Offers', data);
    // Your onPlaceholderException logic here
  };

  const handleRendered = data => {
    console.log('onRendered from Offers');
    // Your onRendered logic here
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Offers</Text>
      <View style={styles.offerContainer}>
        <Text style={styles.offerText}>50% off on selected items!</Text>
        <Text style={styles.offerText}>Hurry, limited time offer.</Text>
      </View>
      <WEInlineWidget
        screenName="offer"
        androidPropertyId={'off1_scr1'}
        style={{height: 200, width: 300, borderWidth: 1}}
        onDataReceived={handleDataReceived}
        onPlaceholderException={handlePlaceholderException}
        onRendered={handleRendered}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  header: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  offerContainer: {
    padding: 20,
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 10,
  },
  offerText: {
    fontSize: 18,
    marginBottom: 10,
  },
});

export default Offers;
