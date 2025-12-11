import { useFocusEffect } from '@react-navigation/native';
import React from 'react';
import {View, Text, StyleSheet, ScrollView, Pressable} from 'react-native';
import {WEInlineWidget} from 'react-native-we-personalization';
import WebEngage from 'react-native-webengage';
import {SCREEN_NAMES, BUTTON_LABELS} from './constants';

interface OrdersScreenProps {
  navigation: any;
}

const OrdersScreen: React.FC<OrdersScreenProps> = ({navigation}) => {
  const orders = [
    {id: '#12345', date: '2024-01-15', status: 'Delivered', total: '$299', items: 2},
    {id: '#12344', date: '2024-01-10', status: 'Shipped', total: '$149', items: 1},
    {id: '#12343', date: '2024-01-05', status: 'Processing', total: '$499', items: 3},
  ];
  const SCREEN_NAME = 'screen2';
  
  const WIDGET_1 = {
    androidPropertyId: 'S2P2',
    iosPropertyId: 22,
    width: 350,
    height: 200,
  };
   // ========== WebEngage Tracking ==========
  const webengage = new WebEngage();
  
  useFocusEffect(
    React.useCallback(() => {
      webengage.screen(SCREEN_NAME);
    }, [])
  );

  // ========== Widget Event Handlers ==========
  const handleWidgetRendered = (data: any) => {
    console.log('Widget rendered:', data);
  };

  const handleWidgetDataReceived = (data: any) => {
    console.log('Widget data received:', data);
  };

  const handleWidgetError = (error: any) => {
    console.log('Widget error:', error);
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Delivered': return '#4CAF50';
      case 'Shipped': return '#2196F3';
      case 'Processing': return '#FF9800';
      default: return '#999';
    }
  };

  return (
    <View style={styles.wrapper}>
      <View style={styles.navButtons}>
        <Pressable style={styles.navButton} onPress={() => navigation.navigate(SCREEN_NAMES.HOME)}>
          <Text style={styles.navButtonText}>{BUTTON_LABELS.HOME}</Text>
        </Pressable>
        <Pressable style={styles.navButton} onPress={() => navigation.navigate(SCREEN_NAMES.CART)}>
          <Text style={styles.navButtonText}>{BUTTON_LABELS.CART}</Text>
        </Pressable>
      </View>
      <ScrollView style={styles.container}>
        <View style={styles.header}>
          <Text style={styles.headerText}>My Orders</Text>
          <Text style={styles.subHeaderText}>{orders.length} orders</Text>
        </View>
      
      {/* Widget 1 - After Categories */}
      <WEInlineWidget
        style={{height: WIDGET_1.height, width: WIDGET_1.width}}
        screenName={SCREEN_NAME}
        androidPropertyId={WIDGET_1.androidPropertyId}
        iosPropertyId={WIDGET_1.iosPropertyId}
        onRendered={handleWidgetRendered}
        onDataReceived={handleWidgetDataReceived}
        onPlaceholderException={handleWidgetError}
      />


      {orders.map(order => (
        <View key={order.id} style={styles.orderCard}>
          <View style={styles.orderHeader}>
            <Text style={styles.orderId}>Order {order.id}</Text>
            <View style={[styles.statusBadge, {backgroundColor: getStatusColor(order.status)}]}>
              <Text style={styles.statusText}>{order.status}</Text>
            </View>
          </View>
          <View style={styles.orderDetails}>
            <Text style={styles.detailText}>Date: {order.date}</Text>
            <Text style={styles.detailText}>Items: {order.items}</Text>
            <Text style={styles.totalText}>Total: {order.total}</Text>
          </View>
        </View>
      ))}
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  wrapper: {
    flex: 1,
  },
  container: {
    flex: 1,
    backgroundColor: '#f0f0f0',
  },
  navButtons: {
    flexDirection: 'row',
    padding: 10,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
  navButton: {
    flex: 1,
    backgroundColor: '#673AB7',
    padding: 10,
    marginHorizontal: 5,
    borderRadius: 5,
    alignItems: 'center',
  },
  navButtonText: {
    color: 'white',
    fontWeight: '600',
  },
  header: {
    backgroundColor: '#673AB7',
    padding: 20,
  },
  headerText: {
    color: 'white',
    fontSize: 28,
    fontWeight: 'bold',
  },
  subHeaderText: {
    color: 'white',
    fontSize: 14,
    marginTop: 5,
  },
  orderCard: {
    backgroundColor: 'white',
    margin: 15,
    padding: 15,
    borderRadius: 12,
    borderLeftWidth: 4,
    borderLeftColor: '#673AB7',
  },
  orderHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 10,
  },
  orderId: {
    fontSize: 18,
    fontWeight: 'bold',
  },
  statusBadge: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 15,
  },
  statusText: {
    color: 'white',
    fontSize: 12,
    fontWeight: '600',
  },
  orderDetails: {
    marginTop: 10,
  },
  detailText: {
    fontSize: 14,
    color: '#666',
    marginBottom: 5,
  },
  totalText: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginTop: 5,
  },
});

export default OrdersScreen;
