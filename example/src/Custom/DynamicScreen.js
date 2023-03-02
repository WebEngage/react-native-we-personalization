import React from 'react';
import {
  Dimensions,
  FlatList,
  ScrollView,
  StyleSheet,
  Text,
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
  const arr = Array.from({ length: size });
  const [screenList, setScreenList] = React.useState([]);
  // TODO- If uncomment WEInlineView will be hidden in android

  React.useEffect(() => {
    (async () => {
      const screenListData = await getValueFromAsyncStorage('screenData');
      const screenArrData = JSON.parse(screenListData);
      setScreenList(screenArrData);
    })();
  }, []);
  React.useEffect(() => {
    if (screenName) {
      webengageInstance.screen(screenName);
    }
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
  });

  const onCampaignClicked = (data) => {
    console.log('dynamic: onCampaignClicked ', data);
    const { deepLink = '' } = data;
    const deepLinkArr = deepLink.split('/');

    if (deepLinkArr.length > 3 && deepLinkArr[2] === 'www.webengage.com') {
      const navigateScreen = deepLinkArr[3];
      screenList.forEach((screenData) => {
        if (screenData.screenName === navigateScreen) {
          navigation.navigate('dynamicScreen', { item: screenData });
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
    console.log(
      'WEZ: Regular onRendered_1 triggered for -',
      d?.targetViewId,
      d
    );
  };

  const onDataReceived_1 = (d) => {
    console.log(
      'WEZ: Regular onDataReceived_1 triggered for ',
      d?.targetViewId,
      d
    );
  };

  const onPlaceholderException_1 = (d) => {
    console.log(
      'WEZ: Regular onPlaceholderException_1 triggered for ',
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
    return <FlatList data={arr} renderItem={renderRecycler} />;
  };

  const renderRegularScreen = () => {
    console.log('arr -', arr);
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
  return <View>{renderScreen()}</View>;
}

const styles = StyleSheet.create({
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
  flatColor: {
    backgroundColor: '#e8b77b',
  },
  textStyle: {
    fontSize: 20,
    // color: '#000',
  },
  box: {
    width: Dimensions.get('window').width,
    height: 200,
    // borderWidth: 10,
    // borderColor: 'red',
    // padding: 50,
  },
});
