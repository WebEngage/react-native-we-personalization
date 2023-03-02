import * as React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import RegularScreen from './RegularScreen';
import ScrollableScreen from './ScrollableScreen';
import FlatListScreen from './FlatListScreen';
import ListScreen from './ListScreen';
import CustomScreens from './Custom/CustomScreens';
import ScreenDetails from './Custom/ScreenDetails';
import DynamicScreen from './Custom/DynamicScreen';

const Stack = createNativeStackNavigator();

const Navigation = () => {
  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName="main">
        <Stack.Screen
          name="main"
          component={ListScreen}
          options={{ title: 'Welcome to App-Inline' }}
        />
        <Stack.Screen
          name="regular"
          component={RegularScreen}
          options={{ title: 'Regular screen' }}
        />
        <Stack.Screen name="scrollable" component={ScrollableScreen} />
        <Stack.Screen name="flatlist" component={FlatListScreen} />
        <Stack.Screen name="customScreens" component={CustomScreens} />
        <Stack.Screen name="screenDetails" component={ScreenDetails} />
        <Stack.Screen
          name="dynamicScreen"
          component={DynamicScreen}
          options={({ route }) => ({ title: route.params.item.screenName })}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
};
export default Navigation;
