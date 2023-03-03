import React from 'react';
import {
  FlatList,
  Modal,
  StyleSheet,
  Text,
  TouchableHighlight,
  View,
} from 'react-native';

export default function NavigationModal(props) {
  console.log('props in Navigation Modal', props);
  const {
    screenList,
    showModal,
    changeModalStatus,
    sendNavigation,
    currentScreen,
  } = props;
  const navigateTO = (item) => {
    console.log('navigateTo ', item);
    sendNavigation(item);
    changeModalStatus(false);
  };
  const renderScreenList = ({ item, index }) => {
    const { screenName = '' } = item;
    console.log('item render ', item);
    if (currentScreen !== screenName)
      return (
        <View style={styles.modalCard}>
          <Text style={styles.screenName}> {screenName}</Text>
          <TouchableHighlight
            style={styles.button}
            onPress={() => navigateTO(item)}
          >
            <Text> Navigate </Text>
          </TouchableHighlight>
        </View>
      );
  };
  return (
    <View style={styles.container}>
      <Modal
        animationType="slide"
        transparent={true}
        visible={showModal}
        onRequestClose={() => {
          changeModalStatus(false);
        }}
      >
        <View style={styles.modalView}>
          <TouchableHighlight
            style={styles.close}
            onPress={() => changeModalStatus(false)}
          >
            <Text> X </Text>
          </TouchableHighlight>
          <FlatList data={screenList} renderItem={renderScreenList} />
        </View>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  modalView: {
    justifyContent: 'center',
    alignSelf: 'center',
    borderWidth: 1,
    backgroundColor: '#d9d9d0',
    padding: 10,
    height: 500,
    width: 300,
    marginVertical: 100,
  },
  close: {
    alignSelf: 'flex-end',
  },
  modalCard: {
    borderWidth: 1,
    borderColor: '#75757344',
    margin: 20,
    paddingVertical: 20,
    paddingHorizontal: 10,
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  button: {
    backgroundColor: '#91e058',
    borderWidth: 1,
    width: 100,
    height: 30,
    borderRadius: 30,
    justifyContent: 'center',
    alignItems: 'center',
  },
  screenName: {
    fontSize: 16,
    fontWeight: '500',
    justifyContent: 'center',
    alignSelf: 'center',
  },
  container: {
    flex: 1,
  },
});
