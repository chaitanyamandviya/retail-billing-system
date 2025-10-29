import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { useAuth } from '../context/AuthContext';

import LoginScreen from '../screens/LoginScreen';
import HomeScreen from '../screens/HomeScreen';
import CreateBillScreen from '../screens/CreateBillScreen';
import TodaysBillsScreen from '../screens/TodaysBillsScreen';

const Stack = createNativeStackNavigator();

export default function AppNavigator() {
  const { user, loading } = useAuth();

  if (loading) {
    return null; // Or a loading screen
  }

  return (
    <NavigationContainer>
      <Stack.Navigator>
        {!user ? (
          <Stack.Screen
            name="Login"
            component={LoginScreen}
            options={{ headerShown: false }}
          />
        ) : (
          <>
            <Stack.Screen
              name="Home"
              component={HomeScreen}
              options={{ title: 'Vandana Retail' }}
            />
            <Stack.Screen
              name="CreateBill"
              component={CreateBillScreen}
              options={{ title: 'Create Bill' }}
            />
            <Stack.Screen
              name="TodaysBills"
              component={TodaysBillsScreen}
              options={{ title: "Today's Bills" }}
            />
          </>
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
}
