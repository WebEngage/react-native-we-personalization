import * as React from 'react';

import {
  StyleSheet,
  View,
  Text,
  Button,
  NativeModules,
  NativeEventEmitter,
} from 'react-native';
import {
  WEPersonalization,
  multiply,
  add,
} from 'react-native-webengage-personalization';
import WebEngage from 'react-native-webengage';

export default function App() {
  const [result, setResult] = React.useState();
  const [addition, setAddition] = React.useState();

  var webengage = new WebEngage();

  React.useEffect(() => {
    multiply(6, 8).then(setResult);
    add(6, 8).then(setAddition);
    // Customs -> WebEngage.registerInline("Property_id","screenName", callback)
  }, []);

  const immediateCallback = () => {
    NativeModules.PersonalizationBridge.immediateCallback(
      'testName',
      'testLocation',
      (error, eventId) => {
        if (error) {
          console.error(`Error found! ${error}`);
        }
        console.log(`event id ${eventId} returned but no Error - ${error}`);
      },
      () => {
        console.log('error callback');
      }
    );
  };

  const promiseCallback = async () => {
    try {
      const eventId = await NativeModules.PersonalizationBridge.promiseCallback(
        'testString',
        'test 2'
      );
      console.log('Event Id received from Java -> ' + eventId);
    } catch (e) {
      console.error(e);
    }
  };

  const listenerCallback = () => {
    NativeModules.PersonalizationBridge.listenerCallback();
  };

  // Values added in event - will be data available here
  const personalizationCallback = (d) => {
    console.log('Personalization callback triggered-', d.nativeEvent);
  };

  React.useEffect(() => {
    const eventEmitter = new NativeEventEmitter(
      NativeModules.PersonalizationBridge
    );
    eventEmitter.addListener('EventReminder', (event) => {
      console.log(event); // "someValue"
    });
  }, []);

  return (
    <View style={styles.container}>
      {/* Banner/Text */}
      <WEPersonalization
        color="#32a852"
        style={styles.box}
        propertyId="123"
        screenName="screen-inline"
        personalizationCallback={personalizationCallback}
        // personalizationCallback="personalizationCallback data"
      />
      <Text>Result: {result}</Text>
      <Text>Addition Result - {addition}</Text>
      <Button
        title="Trigger Immediate Callback from native"
        onPress={immediateCallback}
      />
      <View style={styles.margin20} />
      <Button title="Promise callback Immediate" onPress={promiseCallback} />
      <View style={styles.margin20} />

      <Button
        title="Trigger previously Registered callback(Push click)"
        onPress={listenerCallback}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  margin20: {
    marginTop: 20,
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
