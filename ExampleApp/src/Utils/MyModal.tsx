import CheckBox from '@react-native-community/checkbox';
import React from 'react';
import {Modal, Platform, Pressable, StyleSheet, Text, TextInput, View} from 'react-native';

interface MyModalProps {
  isModalVisible?: boolean;
  setIsModalVisible: (visible: boolean) => void;
  addViewListData: (data: ViewData) => void;
}

interface ViewData {
  position: number;
  height: number;
  width: number;
  propertyId: string | number;
  isCustomView: boolean;
}

const MyModal: React.FC<MyModalProps> = (props) => {
  const {isModalVisible = false, setIsModalVisible, addViewListData} = props;

  const [position, setPosition] = React.useState('1');
  const [height, setHeight] = React.useState('0');
  const [width, setWidth] = React.useState('0');
  const [androidPropertyId, setAndroidPropertyId] = React.useState('');
  const [iosPropertyId, setIosPropertyId] = React.useState('');
  const [isCustomView, setIsCustomView] = React.useState(false);

  const addViewData = () => {
    if (parseInt(position) > 0 && androidPropertyId !== '' && iosPropertyId !== '') {
      const propertyIdToSave = Platform.OS === 'ios' ? parseInt(iosPropertyId, 10) : androidPropertyId;

      const viewData: ViewData = {
        position: parseInt(position, 10) || 0,
        height: parseInt(height, 10) || 0,
        width: parseInt(width, 10) || 0,
        propertyId: propertyIdToSave,
        isCustomView,
      };
      addViewListData(viewData);
    } else {
      setIsModalVisible(false);
      alert('Add valid Data');
    }
  };

  return (
    <View style={{flex: 1}}>
      <Modal animationType="slide" transparent={true} visible={isModalVisible} onRequestClose={() => setIsModalVisible(false)}>
        <View style={{flex: 1, justifyContent: 'center', alignItems: 'center'}}>
          <View style={styles.modalCard}>
            <Text style={styles.headerTxt}>Add View Data!</Text>
            <View style={styles.ViewLine}>
              <Text>Position: </Text>
              <TextInput style={styles.textViewStyle} onChangeText={setPosition} value={position} keyboardType="numeric" />
            </View>
            <View style={styles.ViewLine}>
              <Text>Height: </Text>
              <TextInput style={styles.textViewStyle} onChangeText={setHeight} value={height} keyboardType="numeric" />
            </View>
            <View style={styles.ViewLine}>
              <Text>Width: </Text>
              <TextInput style={styles.textViewStyle} onChangeText={setWidth} value={width} keyboardType="numeric" />
            </View>
            <View style={styles.ViewLine}>
              <Text>Android PropertyId</Text>
              <TextInput style={styles.textViewStyle} onChangeText={setAndroidPropertyId} value={androidPropertyId} autoCapitalize="none" />
            </View>
            <View style={styles.ViewLine}>
              <Text>iOS PropertyId</Text>
              <TextInput style={styles.textViewStyle} onChangeText={setIosPropertyId} value={iosPropertyId} autoCapitalize="none" />
            </View>
            <View style={styles.rowLine}>
              <Text>Custom View: </Text>
              <CheckBox disabled={false} value={isCustomView} style={styles.checkbox} onValueChange={setIsCustomView} />
            </View>
            <Pressable onPress={addViewData} style={[styles.button, styles.btnAdd]}>
              <Text style={styles.btnTxt}>Add View</Text>
            </Pressable>
          </View>
        </View>
      </Modal>
    </View>
  );
};

const styles = StyleSheet.create({
  button: {backgroundColor: '#5e74e0', width: 100, height: 25, borderRadius: 25, alignItems: 'center', justifyContent: 'center', alignSelf: 'center', marginTop: 30},
  btnAdd: {alignSelf: 'flex-start', backgroundColor: '#0d0306', height: 35},
  btnTxt: {color: '#FFFFFF', fontWeight: 'bold'},
  headerTxt: {fontSize: 18, marginTop: 25, textAlign: 'center', fontWeight: 'bold'},
  ViewLine: {marginTop: 30},
  rowLine: {flexDirection: 'row', marginTop: 30},
  textViewStyle: {borderBottomWidth: 1, width: 300, height: 50},
  checkbox: {width: 20, height: 20, marginRight: 20},
  modalCard: {backgroundColor: '#fff', padding: 20},
});

export default MyModal;
