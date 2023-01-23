import React from 'react';
import {
  View,
  Button,
  Text,
  SafeAreaView,
  StyleSheet,
  Pressable,
} from 'react-native';
const ListScreen = ({ navigation }) => {
  return (
    <SafeAreaView style={styles.mainContainer}>
      <Text style={{ marginTop: 20, marginBottom: 50 }}>
        Saving you form In-App notification
      </Text>
      <Pressable
        style={styles.button}
        onPress={() => navigation.navigate('regular')}
      >
        <Text style={styles.textStyle}> Regular </Text>
      </Pressable>

      <Pressable
        style={styles.button}
        onPress={() => navigation.navigate('scrollable')}
      >
        <Text style={styles.textStyle}> Scrollable </Text>
      </Pressable>

      <Pressable
        style={styles.button}
        onPress={() => navigation.navigate('flatlist')}
      >
        <Text style={styles.textStyle}> Flatlist </Text>
      </Pressable>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  mainContainer: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  button: {
    marginBottom: 25,
    backgroundColor: '#91e058',
    borderWidth: 1,
    width: 250,
    height: 50,
    borderRadius: 30,
    justifyContent: 'center',
  },
  textStyle: {
    // color: 'white',
    fontSize: 20,
    textAlign: 'center',
  },
});
export default ListScreen;
