import React, { useRef } from 'react';
import {
  Button,
  Dimensions,
  FlatList,
  Pressable,
  ScrollView,
  StyleSheet,
  Switch,
  Text,
  TextInput,
  TouchableHighlight,
  TouchableOpacity,
  View,
} from 'react-native';
import { webengageInstance } from '../Utils/WebEngageManager';
import { WEInlineView, trackCustomClick, trackCustomImpression } from 'react-native-webengage-personalization';
import {
  registerForCampaigns,
  unRegisterCustomPlaceHolder,
  unRegisterForCampaigns,
  userWillHandleDeepLink,
} from '../../../src';

import PersonalizationBridge from '../../../src/PersonalizationBridge';
import { getValueFromAsyncStorage } from '../Utils';
import NavigationModal from '../Utils/NavigationModal';
import {
  useFocusEffect,
  useNavigation,
  useNavigationState,
} from '@react-navigation/native';
import { registerCustomPlaceHolder } from 'react-native-webengage-personalization'
export default function DynamicScreen(props) {
  const { navigation = {}, route: { params: { item = {} } = {} } = {} } = props;
  const {
    id = 0,
    screenName = '',
    size = 0,
    eventName = '',
    screenProperty = '',
    screenValue = '',
    isRecyclerView = false,
    viewData = [],
  } = item;
  const arr = [];
  const customPropertyList = []
  for (let i = 0; i < size; i++) {
    const id = `item-${i}`;
    arr.push({ id: id });
  }
  const [screenList, setScreenList] = React.useState([]);
  const screenListRef = useRef(null);
  const [customViewLabel, setCustomViewLabel] = React.useState("Custom View: Either campaign not Running / onRendered not triggered")
  const [showNavigation, setShowNavigation] = React.useState(false);
  const [isClickHandledByUser, setIsClickHandledByUser] = React.useState(false);
  const clickRef = useRef(null);
  const [exceptionLable, setExceptionLable] = React.useState("No Exception");
  const [eventNameToTrigger, setEventNameToTrigger] = React.useState("");



  useFocusEffect(
    React.useCallback(() => {
      if (screenName) {
        if(screenProperty && screenValue) {
        console.log('Example: dynamic navigating to  ' + screenName + ' with data', {[screenProperty]: screenValue});
          webengageInstance.screen(screenName, { [screenProperty]: parseInt(screenValue)});
        } else {
        console.log('Example: dynamic navigating to  ' + screenName + " without data" );
        webengageInstance.screen(screenName);
        }
      }
      if (eventName) {
        webengageInstance.track(eventName);
      }
      checkForCustomView()
      return () => {
      };
    }, [])
  );

  React.useEffect(() => {
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
      removeCustomViews()
    };
  }, []);

  React.useEffect(() => {
    userWillHandleDeepLink(isClickHandledByUser);
  }, [isClickHandledByUser]);

  const removeCustomViews = () => {
    customPropertyList.map(property => {
      unRegisterCustomPlaceHolder(property, screenName)
    })
    customPropertyList?.splice(0, customPropertyList.length);
  }


  const checkForCustomView = () => {
    viewData.map( (viewItem) => {
      const {isCustomView =
         false, propertyId} = viewItem
      if(isCustomView) {
        customPropertyList.push(propertyId)
        registerCustomPlaceHolder(
        propertyId,
        screenName,
        onCustomDataReceived,
        onCustomPlaceholderException
        )
        }
    })
  }

  const onCampaignClicked = (data) => {
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
    console.log('Example: dynamic: onCampaignPrepared ', data);
  };

  const onCampaignShown = (data) => {
    console.log('Example: dynamic: onCampaignShown ', data);
  };

  const onCampaignException = (data) => {
    console.log('Example: dynamic: onCampaignException ', data);
  };

  const onRendered_1 = (d) => {
    console.log('Example: Dynamic onRendered triggered for -', d?.targetViewId, d);
  };

  const onDataReceived_1 = (d) => {
    console.log(
      'Example: Dynamic onDataReceived triggered for ',
      d?.targetViewId,
      d
    );
  };

  const onPlaceholderException_1 = (d) => {
    console.log(
      'Example: Dynamic onPlaceholderException triggered for ',
      d?.targetViewId,
      d
    );
    const exceptionText = "Exception occured for id - "+d?.targetViewId+" Exception - "+d?.exception
    setExceptionLable(exceptionText)
  }
   const onCustomDataReceived_1 = (d) => {
    console.log(
      'Example: Dynamic onPlaceholderException triggered for ',
      d?.targetViewId,
      d
    );
   }

  const onCustomDataReceived = (d) => {

    setCustomViewLabel(JSON.stringify(d))
    setIsClickHandledByUser(true)

    console.log(
      'Example: custom onDataReceived!!! triggered for ',
      d?.targetViewId,
      d
    );


  };

  const onCustomPlaceholderException = (d) => {
    console.log(
      'Example: custom onPlaceholderException triggered for ',
      d?.targetViewId,
      d
    );
    const exceptionText = "Exception occured for id - "+d?.targetViewId+" Exception - "+d?.exception
    setExceptionLable(exceptionText)
  };

  const trackImpression = (propertyId) => {
    trackCustomImpression(propertyId, null)
  }

  const trackClick = (propertyId) => {
    trackCustomClick(propertyId, null)
  }

  const renderRecycler = ({ item, index }) => {
    let inlineView = null;
    let isCustomView = false
    viewData?.forEach((viewItem) => {
      const { position,  } = viewItem
      if (position === index) {
        inlineView = viewItem;
        isCustomView = viewItem.isCustomView
      }
    });
    const styleList = isRecyclerView ? styles.flatColor : [];
    if (inlineView) {
      const inlineHeight = inlineView?.height || 250;
      const inlineWidth = inlineView?.width || Dimensions.get('window').width;
      if(isCustomView) {
        return(<View>
          <Text> {customViewLabel} </Text>
          <View style={styles.rowLine}>
            <TouchableHighlight onPress={() => trackImpression(inlineView.propertyId)} style={styles.customButton}>
              <Text>Impression</Text>
            </TouchableHighlight>
            <TouchableHighlight onPress={() => trackClick(inlineView.propertyId)} style={styles.customButton}>
              <Text>Click</Text>
            </TouchableHighlight>
          </View>
        </View>)
      } else {
      return (
        <WEInlineView
          style={[styles.box, { height: inlineHeight, width: inlineWidth }]}
          screenName={screenName}
          propertyId={inlineView.propertyId}
          onRendered={onRendered_1}
          onDataReceived={onDataReceived_1}
          onPlaceholderException={onPlaceholderException_1}
        />
      );
      }
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

  const trackEvent = () => {
    webengageInstance.track(eventNameToTrigger)
  }

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
      <Text> {exceptionLable}</Text>
      <View style={[styles.rowItem, styles.border]}>

      <TextInput style={styles.textInput} placeholder="Enter Event Name" value={eventNameToTrigger} onChangeText={(text) => setEventNameToTrigger(text)} />
      <TouchableOpacity onPress={trackEvent} style={styles.trackButton} >
        <Text style={styles.trackText}> Track  </Text>
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
  customButton: {
    marginTop: 25,
    backgroundColor: '#eb5e67',
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
  border: {
    borderWidth: 1,
    borderColor: '#ccc'
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
  textInput: {
    height: 45,
    borderBottomWidth: 2,
    marginBottom: 2,
    marginHorizontal: 20,
    borderBottomColor: '#ccc',
  },
  trackButton: {
    height: 30,
    width: 100,
    backgroundColor: '#fcc111',
    justifyContent: 'center',
    alignItems: 'center',
    alignSelf: 'center'
  },
  trackText: {
    color: '#000',
    fontSize: 20,
    fontWeight: 'bold',
  },
  box: {
    width: Dimensions.get('window').width,
    height: 200,

    // borderWidth: 10,
    // borderColor: 'red',
    // padding: 50,
  },
});
