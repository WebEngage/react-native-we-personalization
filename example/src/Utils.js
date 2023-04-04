// import AsyncStorage from '@react-native-async-storage/async-storage';
import { AsyncStorage } from 'react-native';
import { MyLogs } from '../../src/MyLogs';


export const saveToAsyncStorage = async (key, val) => {
  try {
    await AsyncStorage.setItem(key, val);
  } catch (error) {
    // Error saving data
    MyLogs('Error while storing data');
  }
};

export const getValueFromAsyncStorage = async (key) => {
  let retVal = '';
  try {
    const value = await AsyncStorage.getItem(key);
    if (value !== null) {
      // We have data!!
      retVal = value;
    }
  } catch (error) {
    // Error retrieving data
  }
  return retVal;
};

export const removeItem = async (key) => {
  try {
    await AsyncStorage.removeItem(key);
  } catch (error) {
    MyLogs(error);
  }
};
