import React, {useRef} from 'react';
import {Dimensions, FlatList, Platform, ScrollView, StyleSheet, Text, TextInput, TouchableHighlight, TouchableOpacity, View} from 'react-native';
import {webengageInstance} from '../Utils/WebEngageManager';
import {WEInlineWidget, trackClick, trackImpression, registerWEPlaceholderCallback, deregisterWEPlaceholderCallback} from 'react-native-we-personalization';
import {getValueFromAsyncStorage} from '../Utils';
import NavigationModal from '../Utils/NavigationModal';
import {useFocusEffect} from '@react-navigation/native';

interface DynamicScreenProps {
  navigation: any;
  route: any;
}

export default function DynamicScreen(props: DynamicScreenProps) {
  const {navigation = {}, route: {params: {item = {}} = {}} = {}} = props;
  const {screenName = '', size = 0, eventName = '', screenProperty = '', screenValue = '', isRecyclerView = false, viewData = []} = item;
  
  const arr: {id: string}[] = [];
  const customPropertyListRef = useRef<any[]>([]);
  for (let i = 0; i < size; i++) {
    arr.push({id: `item-${i}`});
  }

  const [screenList, setScreenList] = React.useState<any[]>([]);
  const screenListRef = useRef<any>(null);
  const [customViewLabel, setCustomViewLabel] = React.useState<any>({});
  const [showNavigation, setShowNavigation] = React.useState(false);
  const [isVisible, setIsVisible] = React.useState(false);
  const clickRef = useRef(null);
  const [exceptionLable, setExceptionLable] = React.useState('No Exception');
  const [eventNameToTrigger, setEventNameToTrigger] = React.useState('');

  useFocusEffect(
    React.useCallback(() => {
      if (screenName) {
        if (screenProperty && screenValue) {
          console.log('Example: dynamic navigating to ' + screenName + ' with data', {[screenProperty]: screenValue});
          webengageInstance.screen(screenName, {[screenProperty]: parseInt(screenValue)});
        } else {
          console.log('Example: dynamic navigating to ' + screenName + ' without data');
          webengageInstance.screen(screenName);
        }
      }
      if (eventName) {
        webengageInstance.track(eventName);
      }
      checkForCustomView();
      return () => {};
    }, [])
  );

  React.useEffect(() => {
    return () => {
      removeCustomViews();
    };
  }, []);

  React.useEffect(() => {
    (async () => {
      const screenListData = await getValueFromAsyncStorage('screenData');
      const screenArrData = JSON.parse(screenListData || '[]');
      setScreenList(screenArrData);
      screenListRef.current = screenArrData;
    })();
  }, []);

  const removeCustomViews = () => {
    customPropertyListRef.current.map((property) => {
      if (property && screenName) {
        console.log('Example: dynamic: Deregistering custom view for -', property, 'screen:', screenName);
        try {
          deregisterWEPlaceholderCallback(property, property, screenName);
        } catch (error) {
          console.error('Example: dynamic: Error deregistering custom view', error);
        }
      }
    });
    customPropertyListRef.current = [];
  };

  const checkForCustomView = () => {
    let customLabels: any = {};
    viewData.map((viewItem: any) => {
      const {isCustomView = false, propertyId: viewPropertyId} = viewItem;
      customLabels = {...customLabels, [viewPropertyId]: 'Custom View: Either campaign not Running / onRendered not triggered'};
      const propertyId = Platform.OS === 'ios' ? viewPropertyId : viewPropertyId;
      if (isCustomView && propertyId && screenName) {
        customPropertyListRef.current.push(propertyId);
        console.log('Example: dynamic: Registering custom view for -', propertyId, 'screen:', screenName);
        try {
          registerWEPlaceholderCallback(viewPropertyId, viewPropertyId, screenName, onCustomDataReceived, onCustomPlaceholderException);
        } catch (error) {
          console.error('Example: dynamic: Error registering custom view', error);
        }
      }
    });
    setCustomViewLabel(customLabels);
  };

  const onRendered1 = (weCampaignData: any) => {
    setIsVisible(true);
    console.log('Example: Dynamic: WEInlineWidget: onRendered triggered for -', weCampaignData?.targetViewId, weCampaignData);
  };

  const onDataReceived1 = (weCampaignData: any) => {
    console.log('Example: Dynamic WEInlineWidget: onDataReceived triggered for ', weCampaignData?.targetViewId, weCampaignData);
  };

  const onPlaceholderException1 = (weCampaignData: any) => {
    console.log('Example: Dynamic WEInlineWidget: onPlaceholderException triggered for ', weCampaignData?.targetViewId, weCampaignData);
    const exceptionText = 'Exception occured for id - ' + weCampaignData?.targetViewId + ' Exception - ' + weCampaignData?.exception;
    setExceptionLable(exceptionText);
  };

  const onCustomDataReceived = (weCampaignData: any) => {
    const {targetViewId} = weCampaignData;
    setCustomViewLabel((prevState: any) => ({...prevState, [targetViewId]: JSON.stringify(weCampaignData)}));
    console.log('Example: custom onDataReceived!!! triggered for ', weCampaignData?.targetViewId, weCampaignData);
  };

  const onCustomPlaceholderException = (weCampaignData: any) => {
    console.log('Example: custom onPlaceholderException triggered for ', weCampaignData?.targetViewId, weCampaignData);
    const exceptionText = 'Exception occured for id - ' + weCampaignData?.targetViewId + ' Exception - ' + weCampaignData?.exception;
    setExceptionLable(exceptionText);
  };

  const trackImpressions = (propertyId: any) => {
    trackImpression(propertyId, null);
  };

  const trackClicks = (propertyId: any) => {
    trackClick(propertyId, null);
  };

  const renderRecycler = ({item, index}: {item: any; index: number}) => {
    let inlineView: any = null;
    let isCustomView = false;
    viewData?.forEach((viewItem: any) => {
      const {position} = viewItem;
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
        return (
          <View>
            <Text> {customViewLabel[inlineView.propertyId]} </Text>
            <View style={styles.rowLine}>
              <TouchableHighlight onPress={() => trackImpressions(inlineView.propertyId)} style={styles.customButton}>
                <Text>Impression</Text>
              </TouchableHighlight>
              <TouchableHighlight onPress={() => trackClicks(inlineView.propertyId)} style={styles.customButton}>
                <Text>Click</Text>
              </TouchableHighlight>
            </View>
          </View>
        );
      } else {
        return (
          <WEInlineWidget
            style={[styles.box, {height: inlineHeight, width: inlineWidth}]}
            screenName={screenName}
            androidPropertyId={inlineView.propertyId}
            iosPropertyId={inlineView.propertyId}
            onRendered={onRendered1}
            onDataReceived={onDataReceived1}
            onPlaceholderException={onPlaceholderException1}
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
    return <FlatList data={arr} keyExtractor={(item) => item.id} renderItem={renderRecycler} />;
  };

  const renderRegularScreen = () => {
    return arr.map((item, index) => renderRecycler({item, index}));
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

  const sendNavigation = (navItem: any) => {
    const {screenName: navigateScreen} = navItem;
    navigation.navigate(navigateScreen, {item: navItem, screenId: navigateScreen});
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
        <TextInput style={styles.textInput} placeholder="Enter Event Name" value={eventNameToTrigger} onChangeText={setEventNameToTrigger} />
        <TouchableOpacity onPress={trackEvent} style={styles.trackButton}>
          <Text style={styles.trackText}> Track </Text>
        </TouchableOpacity>
      </View>
      <NavigationModal screenList={screenList} currentScreen={screenName} showModal={showNavigation} changeModalStatus={setShowNavigation} sendNavigation={sendNavigation} />
      {renderScreen()}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {flex: 1},
  card: {height: 100, backgroundColor: '#e3e1de', borderRadius: 25, margin: 20, borderWidth: 1, borderColor: '#ccc', justifyContent: 'center', alignItems: 'center'},
  button: {marginTop: 25, backgroundColor: '#91e058', borderWidth: 1, width: 100, height: 50, borderRadius: 30, justifyContent: 'center', alignItems: 'center', alignSelf: 'flex-end', marginRight: 25, marginBottom: 20},
  customButton: {marginTop: 25, backgroundColor: '#eb5e67', borderWidth: 1, width: 100, height: 50, borderRadius: 30, justifyContent: 'center', alignItems: 'center', alignSelf: 'flex-end', marginRight: 25, marginBottom: 20},
  rowItem: {flexDirection: 'row', alignSelf: 'center'},
  border: {borderWidth: 1, borderColor: '#ccc'},
  flatColor: {backgroundColor: '#e8b77b'},
  textStyle: {fontSize: 20},
  rowLine: {flexDirection: 'row', justifyContent: 'space-between', marginHorizontal: 20},
  textInput: {height: 45, borderBottomWidth: 2, marginBottom: 2, marginHorizontal: 20, borderBottomColor: '#ccc'},
  trackButton: {height: 30, width: 100, backgroundColor: '#fcc111', justifyContent: 'center', alignItems: 'center', alignSelf: 'center'},
  trackText: {color: '#000', fontSize: 20, fontWeight: 'bold'},
  box: {width: Dimensions.get('window').width, height: 200, alignContent: 'center', justifyContent: 'center', alignItems: 'center', marginLeft: 25 },
});
