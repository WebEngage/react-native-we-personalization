import * as React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import ListScreen from './ListScreen';
import CustomScreens from './Custom/CustomScreens';
import ScreenDetails from './Custom/ScreenDetails';
import DynamicScreen from './Custom/DynamicScreen';
import LoginScreen from './LoginScreen';
import TestScreen from './Custom/TestScreen';
import Offers from './TabScreens/Offers';
import Cart from './TabScreens/Cart';
import MyAccount from './TabScreens/MyAccount';

const Stack = createNativeStackNavigator();
const Tab = createBottomTabNavigator();

export const ScreenNamesContext = React.createContext([]);

const HomeTabNavigator = () => {
  return (
    <Tab.Navigator>
      <Tab.Screen name="Home" component={ListScreen} />
      <Tab.Screen name="Offers" component={Offers} />
      <Tab.Screen name="Cart" component={Cart} />
      <Tab.Screen name="My Account" component={MyAccount} />
    </Tab.Navigator>
  );
};

const Navigation = () => {
  const [screenNames, setScreenNames] = React.useState([]);
  const renderScreenName = () => {
    return screenNames?.map((item) => {
      const { screenName } = item;
      return (
        <Stack.Screen
          key={screenName}
          name={screenName}
          component={DynamicScreen}
          options={{ title: screenName }}
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
            component={HomeTabNavigator}
            options={{ headerShown: false }}
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
          <Stack.Screen name="testScreen" component={TestScreen} />
          <Stack.Screen
            name="dynamicScreen"
            component={DynamicScreen}
            options={({ route }) => ({ title: route.params.item.screenName })}
          />
          {renderScreenName()}
        </Stack.Navigator>
      </NavigationContainer>
    </ScreenNamesContext.Provider>
  );
};

export default Navigation;