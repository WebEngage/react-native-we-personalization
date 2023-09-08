import React, {useEffect} from 'react';
import {View, Text, StyleSheet, Pressable} from 'react-native';
import {webengageInstance} from '../Utils/WebEngageManager';
import WEInlineWidgetTemplate from '../TabScreens/WEInlineWidgetTemplate';

function TestScreen() {
  useEffect(() => {
    webengageInstance.screen('react123');
  }, []);

  const triggerBuy = () => {
    webengageInstance.track('buy');
  };

  const onDataReceivedHandler = data => {
    console.log('onDataReceived');
  };

  const onPlaceholderExceptionHandler = data => {
    console.log('onPlaceholderException ', data);
  };

  const onRenderedHandler = data => {
    console.log('onRendered');
  };

  return (
    <View>
      <Text style={{borderWidth: 1, borderColor: 'green'}}>
        Welcome to Test Screen for Stack Screen!
      </Text>

      <WEInlineWidgetTemplate
        screenName="react123"
        androidPropertyId={'react_banner'}
        iosPropertyId={8759}
        style={styles.inlineStyle}
        onDataReceived={onDataReceivedHandler}
        onPlaceholderException={onPlaceholderExceptionHandler}
        onRendered={onRenderedHandler}
      />
      <Pressable style={styles.button} onPress={triggerBuy}>
        <Text style={styles.btnTxt}> Trigger Buy Event </Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  inlineStyle: {height: 200, width: 300},
  button: {
    backgroundColor: '#5e74e0',
    width: 200,
    height: 40,
    borderRadius: 25,
    alignItems: 'center',
    justifyContent: 'center',
    alignSelf: 'center',
    marginTop: 30,
  },
  btnTxt: {
    color: '#FFFFFF',
    fontSize: 22,
    fontWeight: 'bold',
  },
});

export default TestScreen;
