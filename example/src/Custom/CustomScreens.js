import React from 'react';
import {
  Button,
  FlatList,
  Pressable,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import {
  getValueFromAsyncStorage,
  removeItem,
  saveToAsyncStorage,
} from '../Utils';

export default function CustomScreens({ navigation }) {
  const [screenList, setScreenList] = React.useState([]);

  const addScreen = () => {
    navigation.navigate('screenDetails');
  };
  React.useEffect(() => {
    const unsubscribe = navigation.addListener('focus', async () => {
      const screenData = await getValueFromAsyncStorage('screenData');
      const screenLists = JSON.parse(screenData);
      setScreenList(screenLists);
    });
    return unsubscribe;
  }, [navigation]);

  const removeScreenData = (index) => {
    const screenListData = [...screenList];
    screenListData.splice(index, 1);

    setScreenList(screenListData);
    saveToAsyncStorage('screenData', JSON.stringify(screenListData));
  };

  const openScreen = (item) => {
    navigation.navigate('dynamicScreen', { item });
    // TODO - Navigation to dynamicScreen.js
  };

  const editScreen = (item) => {
    navigation.navigate('screenDetails', { screenData: item, isEdit: true });
  };

  const renderItem = ({ item, index }) => {
    return (
      <View style={styles.item}>
        <View>
          <View style={styles.row}>
            <Text style={styles.textView}>Screen Name</Text>
            <Text style={styles.itemText}>{item.screenName}</Text>
          </View>

          <View style={styles.row}>
            <Text style={styles.textView}>Size</Text>
            <Text style={styles.itemText}>{item.size}</Text>
          </View>

          <View style={styles.row}>
            {/* <Text style={styles.textView}>Screen Name</Text> */}
            {item.isRecyclerView ? (
              <Text style={[styles.itemText, styles.redText]}>RecyclerView</Text>
            ) : (
              <Text style={[styles.itemText, styles.blueText]}>Regular View</Text>
            )}
          </View>
        </View>
        <View>
          <Pressable
            style={[styles.remove, styles.open]}
            onPress={() => openScreen(item)}
          >
            <Text>Open</Text>
          </Pressable>
          <Pressable
            style={[styles.remove, styles.edit]}
            onPress={() => editScreen(item)}
          >
            <Text>Edit</Text>
          </Pressable>
          <Pressable
            style={styles.remove}
            onPress={() => removeScreenData(index)}
          >
            <Text>Remove</Text>
          </Pressable>
        </View>
      </View>
    );
  };

  return (
    <View style={styles.container}>
      <Pressable style={styles.button} onPress={addScreen}>
        <Text style={styles.btnTxt}> Add Screen </Text>
      </Pressable>

      <View style={styles.container}>
        <Text style={styles.headerTxt}> List of Screens Added </Text>
      </View>

      <FlatList
        data={screenList}
        keyExtractor={(item, index) => item.id}
        renderItem={renderItem}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  button: {
    backgroundColor: '#5e74e0',
    width: 100,
    height: 25,
    borderRadius: 25,
    alignItems: 'center',
    justifyContent: 'center',
    alignSelf: 'center',
    marginTop: 30,
  },
  btnTxt: {
    color: '#FFFFFF',
  },
  headerTxt: {
    fontSize: 18,
    marginTop: 25,
    textAlign: 'center',
    fontWeight: 'bold',
  },
  item: {
    marginTop: 20,
    paddingLeft: 20,
    borderRadius: 30,
    paddingVertical: 10,
    backgroundColor: '#dbdbdb',
    flexDirection: 'row',
    flex: 1,
  },
  row: {
    flexDirection: 'row',
    flex: 1,
  },
  textView: {
    // flex: 0.9,
    width: 100,
  },
  itemText: {
    fontWeight: 'bold',
  },
  redText: {
    color: '#ff0000'
  },
  blueText: {
    color: '#00FF'
  },
  remove: {
    width: 100,
    height: 40,
    borderWidth: 1,
    borderRadius: 25,
    justifyContent: 'center',
    alignItems: 'center',
    marginLeft: 50,
    marginTop: 10,
    backgroundColor: '#ba88b4',
  },
  open: {
    backgroundColor: '#66b8f2',
  },
  edit: {
    backgroundColor: '#49b528'
  }
});
