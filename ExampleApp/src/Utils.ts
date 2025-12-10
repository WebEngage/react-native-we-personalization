import AsyncStorage from '@react-native-async-storage/async-storage';

export const saveToAsyncStorage = async (key: string, val: string): Promise<void> => {
  try {
    await AsyncStorage.setItem(key, val);
  } catch (error) {
    console.error('Error while storing data', error);
  }
};

export const getValueFromAsyncStorage = async (key: string): Promise<string> => {
  let retVal = '';
  try {
    const value = await AsyncStorage.getItem(key);
    if (value !== null) {
      retVal = value;
    }
  } catch (error) {
    console.error('Error retrieving data', error);
  }
  return retVal;
};

export const removeItem = async (key: string): Promise<void> => {
  try {
    await AsyncStorage.removeItem(key);
  } catch (error) {
    console.error('Error removing item', error);
  }
};
