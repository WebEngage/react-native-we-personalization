import React, { useRef } from 'react';
import {
  Dimensions,
  FlatList,
  Pressable,
  ScrollView,
  StyleSheet,
  Switch,
  Text,
  TouchableHighlight,
  TouchableOpacity,
  View,
} from 'react-native';
import { webengageInstance } from '../Utils/WebEngageManager';
import { WEInlineView } from 'react-native-webengage-personalization';
import {
  registerForCampaigns,
  unRegisterForCampaigns,
  userWillHandleDeepLink,
} from '../../../src';
import { getValueFromAsyncStorage } from '../Utils';
import NavigationModal from '../Utils/NavigationModal';
import {
  useFocusEffect,
  useNavigation,
  useNavigationState,
} from '@react-navigation/native';
export default function DynamicScreen(props) {
  const { navigation = {}, route: { params: { item = {} } = {} } = {} } = props;

  const {
    id = 0,
    screenName = '',
    size = 0,
    eventName = '',
    isRecyclerView = false,
    viewData = [],
  } = item;
  const arr = [];
  for (let i = 0; i < size; i++) {
    const id = `item-${i}`;
    arr.push({ id: id });
  }
  const [screenList, setScreenList] = React.useState([]);
  const screenListRef = useRef(null);
  const [showNavigation, setShowNavigation] = React.useState(false);
  const [isClickHandledByUser, setIsClickHandledByUser] = React.useState(false);
  const clickRef = useRef(null);

  useFocusEffect(
    React.useCallback(() => {
      if (screenName) {
        webengageInstance.screen(screenName);
      }
      if (eventName) {
        webengageInstance.track(eventName);
      }
      console.log('WEZ: dynamic  ' + screenName + 'mounted');
      return () => {
        console.log('WEZ: dynamic ' + screenName + ' is out of focus');
      };
    }, [])
  );

  React.useEffect(() => {
    // console.log('WEZ: ' + screenName + 'navigted');
    // if (screenName) {
    //   webengageInstance.screen(screenName);
    // }
    // if (eventName) {
    //   webengageInstance.track(eventName);
    // }
    (async () => {
      const screenListData = await getValueFromAsyncStorage('screenData');
      const screenArrData = JSON.parse(screenListData);
      setScreenList(screenArrData);
      screenListRef.current = screenArrData;
    })();
    const callbacks = {
      onCampaignPrepared,
      onCampaignShown,
      onCampaignClicked,
      onCampaignException,
    };
    const doesUserHandelCallbacks = true;
    registerForCampaigns(callbacks);
    return () => {
      unRegisterForCampaigns();
    };
  }, []);

  React.useEffect(() => {
    userWillHandleDeepLink(isClickHandledByUser);
  }, [isClickHandledByUser]);

  // React.useEffect(() => {

  // const callbacks = {
  //   onCampaignPrepared,
  //   onCampaignShown,
  //   onCampaignClicked,
  //   onCampaignException,
  // };
  // const doesUserHandelCallbacks = true;
  // registerForCampaigns(callbacks);
  // userWillHandleDeepLink(doesUserHandelCallbacks);
  // return () => {
  //   unRegisterForCampaigns();
  // };
  // });

  const onCampaignClicked = (data) => {
    console.log('dynamic: onCampaignClicked ', data);
    const { deepLink = '' } = data;
    const deepLinkArr = deepLink.split('/');

    if (
      clickRef.current &&
      deepLinkArr.length > 3 &&
      deepLinkArr[2] === 'www.webengage.com'
    ) {
      const navigateScreen = deepLinkArr[3];
      const screenListArr = screenListRef.current;

      screenListArr.forEach((screenData) => {
        if (screenData.screenName === navigateScreen) {
          navigation.navigate(navigateScreen, { item: screenData });
        }
      });
    }
  };

  const onCampaignPrepared = (data) => {
    console.log('dynamic: onCampaignPrepared ', data);
  };

  const onCampaignShown = (data) => {
    console.log('dynamic: onCampaignShown ', data);
  };

  const onCampaignException = (data) => {
    console.log('dynamic: onCampaignException ', data);
  };

  const onRendered_1 = (d) => {
    console.log('WEZ: Dynamic onRendered triggered for -', d?.targetViewId, d);
  };

  const onDataReceived_1 = (d) => {
    console.log(
      'WEZ: Dynamic onDataReceived triggered for ',
      d?.targetViewId,
      d
    );
  };

  const onPlaceholderException_1 = (d) => {
    console.log(
      'WEZ: Dynamic onPlaceholderException triggered for ',
      d?.targetViewId,
      d
    );
  };

  const renderView = ({ item, index }) => {
    return <View />;
  };

  const renderRecycler = ({ item, index }) => {
    let inlineView = null;
    viewData?.forEach((viewItem) => {
      if (viewItem.position === index) {
        inlineView = viewItem;
      }
    });
    const styleList = isRecyclerView ? styles.flatColor : [];
    if (inlineView) {
      const inlineHeight = inlineView?.height || 250;
      const inlineWidth = inlineView?.width || Dimensions.get('window').width;
      return (
        <WEInlineView
          style={[styles.box, { height: inlineHeight, width: inlineWidth }]}
          screenName={screenName}
          propertyId={inlineView.propertyId} // ak_test_2 - custom
          onRendered={onRendered_1}
          onDataReceived={onDataReceived_1}
          onPlaceholderException={onPlaceholderException_1}
        />
      );
    }
    return (
      <View style={[styles.card, styleList]}>
        <Text style={styles.textStyle}> {index}th Item</Text>
      </View>
    );
  };

  const renderFlatList = () => {
    return (
      <FlatList
        data={arr}
        keyExtractor={(item) => item.id}
        renderItem={renderRecycler}
      />
    );
  };

  const renderRegularScreen = () => {
    return arr.map((item, index) => {
      return renderRecycler({ item, index });
    });
  };
  const renderScreen = () => {
    if (isRecyclerView) {
      return renderFlatList();
    } else {
      return <ScrollView>{renderRegularScreen()}</ScrollView>;
    }
  };
  const toggleSwitch = () => {
    clickRef.current = !isClickHandledByUser;

    setIsClickHandledByUser(!isClickHandledByUser);
  };

  const openNavigation = () => {
    setShowNavigation(true);
  };

  const sendNavigation = (navItem) => {
    const { screenName: navigateScreen } = navItem;
    navigation.navigate(navigateScreen, {
      item: navItem,
      screenId: navigateScreen,
    });
  };

  return (
    <View style={styles.container}>
      <View style={styles.rowLine}>
        <View style={styles.rowItem}>
          <Text> isClickHandledByUser:</Text>
          <Switch
            trackColor={{ false: '#767577', true: '#81b0ff' }}
            thumbColor={isClickHandledByUser ? '#f5dd4b' : '#f4f3f4'}
            ios_backgroundColor="#3e3e3e"
            onValueChange={toggleSwitch}
            value={isClickHandledByUser}
          />
        </View>
        <TouchableOpacity style={styles.button} onPress={openNavigation}>
          <Text> Navigate </Text>
        </TouchableOpacity>
      </View>
      <NavigationModal
        screenList={screenList}
        currentScreen={screenName}
        showModal={showNavigation}
        changeModalStatus={setShowNavigation}
        sendNavigation={sendNavigation}
      />
      {renderScreen()}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  card: {
    height: 100,
    backgroundColor: '#e3e1de',
    borderRadius: 25,
    margin: 20,
    borderWidth: 1,
    borderColor: '#ccc',
    justifyContent: 'center',
    alignItems: 'center',
  },
  button: {
    marginTop: 25,
    backgroundColor: '#91e058',
    borderWidth: 1,
    width: 100,
    height: 50,
    borderRadius: 30,
    justifyContent: 'center',
    alignItems: 'center',
    alignSelf: 'flex-end',
    marginRight: 25,
    marginBottom: 20,
  },
  rowItem: {
    flexDirection: 'row',
    alignSelf: 'center',
  },
  flatColor: {
    backgroundColor: '#e8b77b',
  },
  textStyle: {
    fontSize: 20,
    // color: '#000',
  },
  rowLine: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginHorizontal: 20,
  },
  box: {
    width: Dimensions.get('window').width,
    height: 200,
    // borderWidth: 10,
    // borderColor: 'red',
    // padding: 50,
  },
});
