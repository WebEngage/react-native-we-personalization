import * as React from 'react';

import {
  StyleSheet,
  View,
  Text,
  Button,
  NativeModules,
  NativeEventEmitter,
  Dimensions,
  PixelRatio,
} from 'react-native';
import {
  WEPersonalization,
  multiply,
  add,
} from 'react-native-webengage-personalization';
import WebEngage from 'react-native-webengage';
const RegularScreen = ({ navigation }) => {
  const [result, setResult] = React.useState();
  const [addition, setAddition] = React.useState();

  var webengage = new WebEngage();
  webengage.user.login('Ak112');

  React.useEffect(() => {
    multiply(6, 8).then(setResult);
    add(6, 8).then(setAddition);
    // webengage.screen("ET_home");
  }, []);

  const immediateCallback = () => {
    // webengage.user.setAttribute("toISO", new Date("1995-12-17T03:16:00").toISOString());
    // webengage.user.setAttribute("String date","1995-12-17T03:15:00")
    // webengage.user.setAttribute("date type", new Date("1995-12-17T03:34:00"));

    let date1 = new Date('December 17, 1995 03:24:00'); //December 17, 1995 03:24:00 // invalid date type error
    let date2 = new Date('1995-12-17T03:24:00');
    let date3 = new Date(1995, 11, 17).toISOString(); // the month is 0-indexed
    let date4 = new Date(1995, 11, 17, 3, 24, 0).toISOString();
    let date5 = new Date(628021800000); // passing epoch timestamp // invalid date type error

    webengage.user.setAttribute('zzzdate1', date1);
    webengage.user.setAttribute('zzzdate2', date2);
    webengage.user.setAttribute('zzzdate3', date3);
    webengage.user.setAttribute('zzzdate4', date4);
    webengage.user.setAttribute('zzzdate5', date5);
    webengage.user.setAttribute('zzzdate6', date1);
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
  const personalizationCallback1 = (d) => {
    console.log('PPersonalization callback1 triggered-', d);
  };

  const personalizationCallback2 = (d) => {
    console.log('PPersonalization callback2 triggered-', d);
  };

  React.useEffect(() => {
    const eventEmitter = new NativeEventEmitter(
      NativeModules.PersonalizationBridge
    );
    eventEmitter.addListener('EventReminder', (event) => {
      console.log('Event Listerner called', event); // "someValue"
    });
  }, []);
  return (
    <View style={styles.container}>
      <WEPersonalization
        color="#32a852"
        style={styles.box}
        propertyId="flutter_banner"
        screenName="ET_home"
        personalizationCallback={personalizationCallback1}
      />
      <Text>Result: {result}</Text>
      <Text>Addition Result - {addition}</Text>
      <Button
        title="Trigger Immediate Callback from native"
        onPress={immediateCallback}
      />
      <WEPersonalization
        color="#12023f"
        style={styles.box2}
        propertyId="flutter_text"
        screenName="ET_home"
        personalizationCallback={personalizationCallback2}
        // personalizationCallback="personalizationCallback data"
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
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  margin20: {
    marginTop: 20,
  },
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
    height: 200,
    borderWidth: 10,
    borderColor: 'red',
    padding: 50,
  },
});
export default RegularScreen;