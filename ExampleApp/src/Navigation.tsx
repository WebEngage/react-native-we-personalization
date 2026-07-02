import * as React from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import ListScreen from './ListScreen';
import CustomScreens from './Custom/CustomScreens';
import ScreenDetails from './Custom/ScreenDetails';
import DynamicScreen from './Custom/DynamicScreen';
import LoginScreen from './LoginScreen';
import HomeScreen from './HomeScreen';
import OrdersScreen from './OrdersScreen';
import CartScreen from './CartScreen';
import {SCREEN_NAMES, BUTTON_LABELS} from './constants';

const Stack = createNativeStackNavigator();

export const ScreenNamesContext = React.createContext<[any[], React.Dispatch<React.SetStateAction<any[]>>]>([[], () => {}]);

const Navigation = () => {
  const [screenNames, setScreenNames] = React.useState<any[]>([]);
  
  const renderScreenName = () => {
    return screenNames?.map((item) => {
      const {screenName} = item;
      return (
        <Stack.Screen
          key={screenName}
          name={screenName}
          component={DynamicScreen}
          options={{title: screenName}}
        />
      );
    });
  };

  return (
    <ScreenNamesContext.Provider value={[screenNames, setScreenNames]}>
      <NavigationContainer>
        <Stack.Navigator initialRouteName="main">
          <Stack.Screen
            name="main"
            component={ListScreen}
            options={{title: 'Welcome to App-Inline'}}
          />
          <Stack.Screen
            name="login"
            component={LoginScreen}
            options={{
              title: 'Login',
              headerBackVisible: false,
            }}
          />
          <Stack.Screen name="customScreens" component={CustomScreens} />
          <Stack.Screen name="screenDetails" component={ScreenDetails} />
          <Stack.Screen
            name="dynamicScreen"
            component={DynamicScreen}
            options={({route}: any) => ({title: route.params.item.screenName})}
          />
          <Stack.Screen
            name={SCREEN_NAMES.HOME}
            component={HomeScreen}
            options={{title: BUTTON_LABELS.HOME}}
          />
          <Stack.Screen
            name={SCREEN_NAMES.ORDERS}
            component={OrdersScreen}
            options={{title: BUTTON_LABELS.ORDERS}}
          />
          <Stack.Screen
            name={SCREEN_NAMES.CART}
            component={CartScreen}
            options={{title: BUTTON_LABELS.CART}}
          />
          {renderScreenName()}
        </Stack.Navigator>
      </NavigationContainer>
    </ScreenNamesContext.Provider>
  );
};

export default Navigation;
