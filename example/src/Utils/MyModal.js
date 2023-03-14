import CheckBox from '@react-native-community/checkbox';
import React, { useState } from 'react';
import {
  Modal,
  Platform,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';

const MyModal = (props) => {
  const { isModalVisible = false, setIsModalVisible, addViewListData } = props;

  const [position, setPosition] = React.useState(1);
  const [height, setHeight] = React.useState(0);
  const [width, setWidth] = React.useState(0);
  const [propertyId, setPropertyId] = React.useState('');
  const [isCustomView, setIsCustomView] = React.useState(false);

  const addViewData = () => {
    if (position > 0 && propertyId != '') {
      const propertyIdToSave =
        Platform.OS === 'ios' ? parseInt(propertyId, 10) : propertyId;

      const viewData = {
        position: parseInt(position, 10) || 0,
        height: parseInt(height, 10) || 0,
        width: parseInt(width, 10) || 0,
        propertyId: propertyIdToSave,
        isCustomView
      };
      addViewListData(viewData);
    } else {
      setIsModalVisible(false);
      alert('Add valid Data');
    }
  };
  const propertyIdLabel =
    Platform.OS === 'ios' ? 'Property Id:(Numeric)' : 'PropertyId: ';

  return (
    <View style={{ flex: 1 }}>
      <Modal
        animationType="slide"
        transparent={true}
        visible={isModalVisible}
        onRequestClose={() => {
          setIsModalVisible(false);
        }}
      >
        <View
          style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}
        >
          <View style={styles.modalCard}>
            <Text style={styles.headerTxt}>Add View Data!</Text>
            <View style={styles.ViewLine}>
              <Text>Position: </Text>
              <TextInput
                style={styles.textViewStyle}
                onChangeText={(val) => setPosition(val)}
                value={position}
                keyboardType="numeric"
              />
            </View>

            <View style={styles.ViewLine}>
              <Text>Height: </Text>
              <TextInput
                style={styles.textViewStyle}
                onChangeText={(val) => setHeight(val)}
                value={height}
                keyboardType="numeric"
              />
            </View>

            <View style={styles.ViewLine}>
              <Text>Width: </Text>
              <TextInput
                style={styles.textViewStyle}
                onChangeText={(val) => setWidth(val)}
                value={width}
                keyboardType="numeric"
              />
            </View>

            <View style={styles.ViewLine}>
              <Text>{propertyIdLabel} </Text>
              <TextInput
                style={styles.textViewStyle}
                onChangeText={(val) => setPropertyId(val)}
                value={propertyId}
              />
            </View>


            <View style={styles.rowLine}>
          <Text>Custom View: </Text>

          <CheckBox
            offAnimationType="stroke"
            onFillColor={'blue'}
            onCheckColor={'white'}
            disabled={false}
            value={isCustomView}
            style={styles.checkbox}
            onValueChange={(newValue) => setIsCustomView(newValue)}
          />
        </View>

            {/* <TouchableOpacity onPress={() => setIsModalVisible(false)}>
              <Text>Close Modal</Text>
            </TouchableOpacity> */}
            <Pressable
              onPress={addViewData}
              style={[styles.button, styles.btnAdd]}
            >
              <Text style={styles.btnTxt}> Add View </Text>
            </Pressable>
          </View>
        </View>
      </Modal>
    </View>
  );
};

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
  btnAdd: {
    alignSelf: 'flex-start',
    backgroundColor: '#0d0306',
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
  textViewStyle: {
    borderBottomWidth: 1,
    width: 300,
    height: 50,
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
  modalCard: { backgroundColor: '#fff', padding: 20 },
});

export default MyModal;
