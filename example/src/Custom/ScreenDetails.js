import React, { useState } from 'react';
import {
  FlatList,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  View,
  ScrollView,
  Button,
} from 'react-native';
import CheckBox from '@react-native-community/checkbox';
import { getValueFromAsyncStorage, saveToAsyncStorage } from '../Utils';
import MyModal from '../Utils/MyModal';

export default function ScreenDetails(props) {
  const { navigation, route = {} } = props;
  const { params = {} } = route;
  const { screenData = {}, isEdit = false, itemIndex = 0 } = params;
  const [size, setSize] = React.useState(screenData?.size || 0);
  const [screenName, setScreenName] = React.useState(
    screenData?.screenName || ''
  );
  const [eventName, setEventName] = React.useState(screenData?.eventName || '');
  const [isRecyclerView, setIsRecyclerView] = useState(
    screenData?.isRecyclerView || false
  );
  const [screenList, setScreenList] = React.useState([]);
  const [viewData, setViewData] = React.useState(screenData?.viewData || []);
  const [isModalVisible, setIsModalVisible] = React.useState(false);

  React.useEffect(() => {
    (async () => {
      const screenListData = await getValueFromAsyncStorage('screenData');
      const screenArrData = JSON.parse(screenListData);
      setScreenList(screenArrData);
    })();
  }, []);

  const onSizeChange = (value) => {
    setSize(value);
  };

  const onScreenChange = (value) => {
    setScreenName(value);
  };

  const onEventChange = (value) => {
    setEventName(value);
  };

  const addScreenDetails = () => {
    const length = screenList.length + 1;
    if (size > 0 && screenName) {
      // Store Data
      const randomNumber = Math.floor(Math.random() * 1000 + 1);
      const screenData = {
        size,
        screenName,
        eventName,
        isRecyclerView,
        viewData,
        id: randomNumber,
      };

      const screenListData = [...screenList];
      const index = screenListData.findIndex(
        (item) => item.screenName === screenName
      );
      if (isEdit) {
        if (index === itemIndex) {
          screenListData[itemIndex] = screenData;
          saveToAsyncStorage('screenData', JSON.stringify(screenListData));
          navigation.goBack();
        } else {
          alert("screen Name already Exists")
        }
      } else {
        if (index !== -1) {
          // screenListData.splice(index, 1, screenData);
          alert('Screen Already Exists');
        } else {
          screenListData.push(screenData);
          saveToAsyncStorage('screenData', JSON.stringify(screenListData));
          navigation.goBack();
          alert('Added screen data');
        }
      }
    }
  };

  const addViewData = () => {
    // setIsModalVisible(true);
    changeModalVisiblity(true);
  };

  const changeModalVisiblity = (value) => {
    setIsModalVisible(value);
  };

  const addViewListData = (data) => {
    const viewList = [...viewData];

    let isDuplicate = false;

    for (const viewItem of viewList) {
      if (
        viewItem.propertyId === data.propertyId ||
        viewItem.position === data.position ||
        data.position < 0 ||
        data.propertyId === ''
      ) {
        isDuplicate = true;
        break;
      }
    }

    if (!isDuplicate) {
      viewList.push(data);
      setViewData(viewList);
    } else {
      alert('Invalid PropertyId||Position');
    }

    setIsModalVisible(false);
  };
  const renderRow = (item) => {
    return Object.entries(item).map(([key, value]) => {
      return (
        <View style={styles.cardRow}>
          <Text style={styles.cardTitle}> {key}</Text>
          <Text style={styles.cardText}> {value}</Text>
        </View>
      );
    });
  };

  const deleteView = (item, index) => {
    const viewList = [...viewData];
    viewList.splice(index, 1);
    setViewData(viewList);
    // const list = getValueFromAsyncStorage("screenData")
  };

  const renderView = (item, index) => {
    return (
      <View style={styles.cardView}>
        {renderRow(item)}
        {/* <Button title="delete" onPress={() => deleteView(index)} /> */}
        <Pressable style={styles.deletebtn} onPress={() => deleteView(index)}>
          <Text> Delete </Text>
        </Pressable>
      </View>
    );
  };

  const viewListDisplay = () => {
    if (viewData.length) {
      return viewData.map((item, index) => {
        return renderView(item, index);
      });
    }
    return null;
  };

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.headerTxt}>Add Screen Details</Text>
      <View style={styles.form}>
        <View style={styles.ViewLine}>
          <Text>Size: </Text>
          <TextInput
            style={styles.textViewStyle}
            onChangeText={onSizeChange}
            value={size}
            keyboardType="numeric"
          />
        </View>

        <View style={styles.ViewLine}>
          <Text>Screen Name: </Text>
          <TextInput
            style={styles.textViewStyle}
            onChangeText={onScreenChange}
            autoCapitalize="none"
            autoCorrect={false}
            value={screenName}
          />
        </View>

        <View style={styles.ViewLine}>
          <Text>Event Name: </Text>
          <TextInput
            style={styles.textViewStyle}
            onChangeText={onEventChange}
            value={eventName}
          />
        </View>

        <View style={styles.rowLine}>
          <CheckBox
            offAnimationType="stroke"
            onFillColor={'blue'}
            onCheckColor={'white'}
            disabled={false}
            value={isRecyclerView}
            style={styles.checkbox}
            onValueChange={(newValue) => setIsRecyclerView(newValue)}
          />
          <Text>Recycler View: </Text>
        </View>
        <MyModal
          isModalVisible={isModalVisible}
          setIsModalVisible={changeModalVisiblity}
          addViewListData={addViewListData}
        />
        <View style={styles.rowBtn}>
          <Pressable
            onPress={addViewData}
            style={[styles.button, styles.btnAdd]}
          >
            <Text style={styles.btnTxt}> Add View </Text>
          </Pressable>
          <Pressable
            onPress={addScreenDetails}
            style={[styles.button, styles.btnSave]}
          >
            <Text style={styles.btnTxt}> Save Screen </Text>
          </Pressable>
        </View>
        {viewListDisplay()}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    borderWidth: 1,
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
  deletebtn: {
    backgroundColor: '#e0a375',
    width: 100,
    height: 25,
    borderRadius: 25,
    alignItems: 'center',
    justifyContent: 'center',
    alignSelf: 'center',
    marginTop: 20,
  },
  btnAdd: {
    alignSelf: 'flex-start',
    backgroundColor: '#0d0306',
    height: 35,
  },
  btnSave: {
    alignSelf: 'flex-start',
    backgroundColor: '#1626db',
    height: 35,
  },
  btnTxt: {
    color: '#FFFFFF',
    fontWeight: 'bold',
  },
  headerTxt: {
    fontSize: 18,
    marginTop: 25,
    textAlign: 'center',
    fontWeight: 'bold',
  },
  ViewLine: {
    // flexDirection: 'row',
    marginTop: 30,
  },
  rowLine: {
    flexDirection: 'row',
    marginTop: 30,
  },
  rowBtn: {
    flexDirection: 'row',
    justifyContent: 'space-evenly',
  },
  textViewStyle: {
    borderBottomWidth: 1,
    width: 300,
    height: 50,
    // marginTop: 10,
  },
  form: {
    marginTop: 50,
    margin: 20,
  },
  checkbox: {
    width: 20,
    height: 20,
    marginRight: 20,
  },
  cardView: {
    marginTop: 20,
    // borderWidth: 1,
    borderRadius: 30,
    paddingVertical: 10,
    backgroundColor: '#dbdbdb',
  },
  cardTitle: {
    marginLeft: 25,
    flex: 0.5,
    fontSize: 15,
    fontWeight: 'bold',
  },
  cardText: {
    fontSize: 15,
  },
  cardRow: {
    flexDirection: 'row',
    // justifyContent: 'space-evenly'
  },
});
