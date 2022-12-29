import * as React from 'react';

import { StyleSheet, View, Text, Button, NativeModules, NativeEventEmitter } from 'react-native';
import {
  WebengagePersonalizationView,
  multiply,
  add,
} from 'react-native-webengage-personalization';

export default function App() {
  const [result, setResult] = React.useState<number | undefined>();
  const [addition, setAddition] = React.useState<number | undefined>();
  React.useEffect(() => {
    multiply(6, 8).then(setResult);
    add(6, 8).then(setAddition);
  }, []);

  const immediateCallback = () => {
    NativeModules.PersonalizationBridge.immediateCallback(
      'testName',
      'testLocation',
      (error: object, eventId: string) => {
        if (error) {
          console.error(`Error found! ${error}`);
        }
        console.log(`event id ${eventId} returned`);
      }
    );
  };

  const promiseCallback = async () => {
    try {
      const eventId = await NativeModules.PersonalizationBridge.promiseCallback(
        'testString'
      );
      console.log('Event Id received from Java -> ' + eventId);
    } catch (e) {
      console.error(e);
    }
  };

  const listenerCallback = () => {
    NativeModules.PersonalizationBridge.listenerCallback()
  }

  React.useEffect(() => {
   const eventEmitter = new NativeEventEmitter(NativeModules.PersonalizationBridge);
   eventEmitter.addListener('EventReminder', (event) => {
      console.log(event) // "someValue"
   });
  }, [])


  return (
    <View style={styles.container}>
      <WebengagePersonalizationView color="#32a852" style={styles.box} />
      <Text>Result: {result}</Text>
      <Text>Addition Result - {addition}</Text>
      <Button
        title="Trigger Immediate Callback from native"
        onPress={immediateCallback}
      />
      <View style={styles.margin20} />
      <Button title="Promise callback Immediate" onPress={promiseCallback} />
      <View style={styles.margin20} />

      <Button title="Trigger previously Registered callback(Push click)" onPress={listenerCallback} />
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
