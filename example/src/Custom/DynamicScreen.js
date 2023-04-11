import React, {useRef} from 'react';
import {
  Dimensions,
  FlatList,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableHighlight,
  TouchableOpacity,
  View,
} from 'react-native';
import {webengageInstance} from '../Utils/WebEngageManager';
import {WEInlineWidget, trackClick, trackImpression, registerWECampaignCallback,
  registerWEPlaceholderCallback,
  deregisterWEPlaceholderCallback,
  deregisterWECampaignCallback,
} from 'react-native-webengage-personalization';
import {getValueFromAsyncStorage} from '../Utils';
import NavigationModal from '../Utils/NavigationModal';
import {useFocusEffect} from '@react-navigation/native';

export default function DynamicScreen(props) {
  const {navigation = {}, route: {params: {item = {}} = {}} = {}} = props;
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
  const customPropertyList = [];
  for (let i = 0; i < size; i++) {
    const id = `item-${i}`;
    arr.push({id: id});
  }
  const [screenList, setScreenList] = React.useState([]);
  const screenListRef = useRef(null);
  const [customViewLabel, setCustomViewLabel] = React.useState({});
  const [showNavigation, setShowNavigation] = React.useState(false);

  const clickRef = useRef(null);
  const [exceptionLable, setExceptionLable] = React.useState('No Exception');
  const [eventNameToTrigger, setEventNameToTrigger] = React.useState('');


  useFocusEffect(
      React.useCallback(() => {
        if (screenName) {
          if (screenProperty && screenValue) {
            console.log('Example: dynamic navigating to  ' + screenName + ' with data', {[screenProperty]: screenValue});
            webengageInstance.screen(screenName, {[screenProperty]: parseInt(screenValue)});
          } else {
            console.log('Example: dynamic navigating to  ' + screenName + ' without data' );
            webengageInstance.screen(screenName);
          }
        }
        if (eventName) {
          webengageInstance.track(eventName);
        }
        checkForCustomView();

        return () => {
        };
      }, []),
  );

  React.useEffect(() => {
    (async () => {
      const screenListData = await getValueFromAsyncStorage('screenData');
      const screenArrData = JSON.parse(screenListData);
      setScreenList(screenArrData);
      screenListRef.current = screenArrData;
    })();

    const WECampaignCallback = {
      onCampaignPrepared,
      onCampaignShown,
      onCampaignClicked,
      onCampaignException,
    };
    registerWECampaignCallback(WECampaignCallback);
    return () => {
      deregisterWECampaignCallback();
      removeCustomViews();
    };
  }, []);


  const removeCustomViews = () => {
    customPropertyList.map((property) => {
      const androidPropertyId = property;
      const iosPropertyId = property;
      deregisterWEPlaceholderCallback(androidPropertyId, iosPropertyId, screenName);
    });
    customPropertyList?.splice(0, customPropertyList.length);
  };


  const checkForCustomView = () => {
    let customLabels = {};
    viewData.map( (viewItem) => {
      const {isCustomView = false, propertyId: viewPropertyId} = viewItem;
      customLabels = {...customLabels, [viewPropertyId]: 'Custom View: Either campaign not Running / onRendered not triggered'};
      const iosPropertyId = viewPropertyId;
      const androidPropertyId = viewPropertyId;
      const propertyId = Platform.OS === 'ios' ? iosPropertyId : androidPropertyId;
      if (isCustomView) {
        customPropertyList.push(propertyId);
        registerWEPlaceholderCallback(
            androidPropertyId,
            iosPropertyId,
            screenName,
            onCustomDataReceived,
            onCustomPlaceholderException,
        );
      }
    });
    setCustomViewLabel(customLabels);
  };

  const onCampaignClicked = (data) => {
    const {deepLink = ''} = data;
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
          navigation.navigate(navigateScreen, {item: screenData});
        }
      });
    }
  };

  // data should weCampaignData
  const onCampaignPrepared = (weCampaignData) => {
    console.log('Example: dynamic: onCampaignPrepared ', weCampaignData);
  };

  const onCampaignShown = (weCampaignData) => {
    console.log('Example: dynamic: onCampaignShown ', weCampaignData);
  };

  const onCampaignException = (weCampaignData) => {
    console.log('Example: dynamic: onCampaignException ', weCampaignData);
  };

  const onRendered_1 = (weCampaignData) => {
    console.log('Example: Dynamic onRendered triggered for -', weCampaignData?.targetViewId, weCampaignData);
  };

  const onDataReceived_1 = (weCampaignData) => {
    console.log(
        'Example: Dynamic onDataReceived triggered for ',
        weCampaignData?.targetViewId,
        weCampaignData,
    );
  };

  const onPlaceholderException_1 = (weCampaignData) => {
    console.log(
        'Example: Dynamic onPlaceholderException triggered for ',
        weCampaignData?.targetViewId,
        weCampaignData,
    );
    const exceptionText = 'Exception occured for id - '+weCampaignData?.targetViewId+' Exception - '+weCampaignData?.exception;
    setExceptionLable(exceptionText);
  };

  const onCustomDataReceived = (weCampaignData) => {
    const {targetViewId} = weCampaignData;
    setCustomViewLabel((prevState) => ({
      ...prevState,
      [targetViewId]: JSON.stringify(weCampaignData),
    }));
    console.log(
        'Example: custom onDataReceived!!! triggered for ',
        weCampaignData?.targetViewId,
        weCampaignData,
        customViewLabel,
    );
  };

  const onCustomPlaceholderException = (weCampaignData) => {
    console.log(
        'Example: custom onPlaceholderException triggered for ',
        weCampaignData?.targetViewId,
        weCampaignData,
    );
    const exceptionText = 'Exception occured for id - '+weCampaignData?.targetViewId+' Exception - '+weCampaignData?.exception;
    setExceptionLable(exceptionText);
  };

  const trackImpressions = (propertyId) => {
    trackImpression(propertyId, null);
  };

  const trackClicks = (propertyId) => {
    trackClick(propertyId, null);
  };

  const renderRecycler = ({item, index}) => {
    let inlineView = null;
    let isCustomView = false;
    viewData?.forEach((viewItem) => {
      const {position, propertyId} = viewItem;
      if (position === index) {
        inlineView = viewItem;
        isCustomView = viewItem.isCustomView;
      }
    });
    const styleList = isRecyclerView ? styles.flatColor : [];
    if (inlineView) {
      const inlineHeight = inlineView?.height || 250;
      const inlineWidth = inlineView?.width || Dimensions.get('window').width;
      if (isCustomView) {
        return (<View>
          <Text> {customViewLabel[inlineView.propertyId]} </Text>
          <View style={styles.rowLine}>
            <TouchableHighlight onPress={() => trackImpressions(inlineView.propertyId)} style={styles.customButton}>
              <Text>Impression</Text>
            </TouchableHighlight>
            <TouchableHighlight onPress={() => trackClicks(inlineView.propertyId)} style={styles.customButton}>
              <Text>Click</Text>
            </TouchableHighlight>
          </View>
        </View>);
      } else {
        return (
          <WEInlineWidget
            style={[styles.box, {height: inlineHeight, width: inlineWidth}]}
            screenName={screenName}
            androidPropertyId={inlineView.propertyId}
            iosPropertyId={inlineView.propertyId}
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
    console.log('customViewLabel in regular - ', customViewLabel);

    return arr.map((item, index) => {
      return renderRecycler({item, index});
    });
  };
  const renderScreen = () => {
    if (isRecyclerView) {
      return renderFlatList();
    } else {
      return <ScrollView>{renderRegularScreen()}</ScrollView>;
    }
  };

  const openNavigation = () => {
    setShowNavigation(true);
  };

  const trackEvent = () => {
    webengageInstance.track(eventNameToTrigger);
  };

  const sendNavigation = (navItem) => {
    const {screenName: navigateScreen} = navItem;
    navigation.navigate(navigateScreen, {
      item: navItem,
      screenId: navigateScreen,
    });
  };

  return (
    <View style={styles.container}>
      <View style={styles.rowLine}>

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
    borderColor: '#ccc',
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
    alignSelf: 'center',
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
