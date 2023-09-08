import React from 'react';
import {View, Text, StyleSheet} from 'react-native';
import {webengageInstance} from '../Utils/WebEngageManager';
import {useIsFocused} from '@react-navigation/native';
import WEInlineWidgetTemplate from './WEInlineWidgetTemplate';

const Offers = () => {
  const isFocused = useIsFocused();

  React.useEffect(() => {
    if (isFocused) {
      // The screen is focused, you can track your screen here
      console.log('Offer screen is focused');
      webengageInstance.screen('offer');
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
      <WEInlineWidgetTemplate
        screenName="offer"
        androidPropertyId={'off1_scr1'}
        iosPropertyId={75991}
        style={styles.weINlineStyle}
        onDataReceived={handleDataReceived}
        onPlaceholderException={handlePlaceholderException}
        onRendered={handleRendered}
      />
      <Text style={styles.welcomeText}> Welcome to Offers!</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  welcomeText: {
    fontSize: 24,
    fontWeight: 'bold',
    textAlign: 'center',
    marginTop: 20,
  },
  weINlineStyle: {
    height: 200,
    width: 300,
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
