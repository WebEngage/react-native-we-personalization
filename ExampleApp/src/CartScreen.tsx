import { useFocusEffect } from '@react-navigation/native';
import React from 'react';
import {View, Text, StyleSheet, ScrollView, Pressable, Platform, TouchableHighlight} from 'react-native';
import {SCREEN_NAMES, BUTTON_LABELS} from './constants';
import {
  registerWEPlaceholderCallback,
  deregisterWEPlaceholderCallback,
  trackClick,
  trackImpression,
} from 'react-native-we-personalization';
import WebEngage from 'react-native-webengage';

interface CartScreenProps {
  navigation: any;
}

const CartScreen: React.FC<CartScreenProps> = ({navigation}) => {
  // ========== WebEngage Configuration ==========
  const SCREEN_NAME = 'screen3';
  const ANDROID_PROPERTY_ID = 'S3P1';
  const IOS_PROPERTY_ID = 31;

  const [placeholderData, setPlaceholderData] = React.useState<string>('Waiting for data...');

  // ========== WebEngage Tracking ==========
  const webengage = new WebEngage();
  
  useFocusEffect(
    React.useCallback(() => {
      webengage.screen(SCREEN_NAME);
    }, [])
  );

  // ========== Placeholder Callbacks ==========
  const onDataReceived = (data: any) => {
    console.log('Placeholder data received:', data);
    setPlaceholderData(JSON.stringify(data, null, 2));
  };

  const onPlaceholderException = (error: any) => {
    console.log('Placeholder exception:', error);
    setPlaceholderData(`Error: ${JSON.stringify(error, null, 2)}`);
  };

  // ========== Track Handlers ==========
  const handleTrackImpression = () => {
    const propertyId = Platform.OS === 'ios' ? IOS_PROPERTY_ID.toString() : ANDROID_PROPERTY_ID;
    trackImpression(propertyId);
  };

  const handleTrackClick = () => {
    const propertyId = Platform.OS === 'ios' ? IOS_PROPERTY_ID.toString() : ANDROID_PROPERTY_ID;
    trackClick(propertyId);
  };

  // ========== Lifecycle ==========
  React.useEffect(() => {
    registerWEPlaceholderCallback(
      ANDROID_PROPERTY_ID,
      IOS_PROPERTY_ID,
      SCREEN_NAME,
      onDataReceived,
      onPlaceholderException
    );

    return () => {
      deregisterWEPlaceholderCallback(ANDROID_PROPERTY_ID, IOS_PROPERTY_ID, SCREEN_NAME);
    };
  }, []);

  // ========== Cart Data ==========
  const cartItems = [
    {id: 1, name: 'Wireless Mouse', price: 29.99, quantity: 2},
    {id: 2, name: 'USB-C Cable', price: 12.99, quantity: 1},
    {id: 3, name: 'Phone Case', price: 19.99, quantity: 3},
  ];

  const subtotal = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
  const tax = subtotal * 0.1;
  const total = subtotal + tax;

  return (
    <View style={styles.container}>
      <View style={styles.navButtons}>
        <Pressable style={styles.navButton} onPress={() => navigation.navigate(SCREEN_NAMES.HOME)}>
          <Text style={styles.navButtonText}>{BUTTON_LABELS.HOME}</Text>
        </Pressable>
        <Pressable style={styles.navButton} onPress={() => navigation.navigate(SCREEN_NAMES.ORDERS)}>
          <Text style={styles.navButtonText}>{BUTTON_LABELS.ORDERS}</Text>
        </Pressable>
      </View>
      <ScrollView style={styles.itemsContainer}>
        <Text style={styles.title}>Shopping Cart ({cartItems.length} items)</Text>

        {/* Placeholder Data Display */}
        <View style={styles.dataBox}>
          <Text style={styles.dataTitle}>Placeholder Data:</Text>
          <ScrollView style={styles.dataScroll}>
            <Text style={styles.dataText}>{placeholderData}</Text>
          </ScrollView>
          <View style={styles.buttonRow}>
            <TouchableHighlight style={styles.trackButton} onPress={handleTrackImpression}>
              <Text style={styles.trackButtonText}>Track Impression</Text>
            </TouchableHighlight>
            <TouchableHighlight style={styles.trackButton} onPress={handleTrackClick}>
              <Text style={styles.trackButtonText}>Track Click</Text>
            </TouchableHighlight>
          </View>
        </View>
        
        {cartItems.map(item => (
          <View key={item.id} style={styles.cartItem}>
            <View style={styles.itemImage} />
            <View style={styles.itemDetails}>
              <Text style={styles.itemName}>{item.name}</Text>
              <Text style={styles.itemPrice}>${item.price.toFixed(2)}</Text>
              <View style={styles.quantityContainer}>
                <Text style={styles.quantityLabel}>Qty: </Text>
                <Text style={styles.quantityValue}>{item.quantity}</Text>
              </View>
            </View>
            <Text style={styles.itemTotal}>${(item.price * item.quantity).toFixed(2)}</Text>
          </View>
        ))}
      </ScrollView>

      <View style={styles.summaryContainer}>
        <View style={styles.summaryRow}>
          <Text style={styles.summaryLabel}>Subtotal:</Text>
          <Text style={styles.summaryValue}>${subtotal.toFixed(2)}</Text>
        </View>
        <View style={styles.summaryRow}>
          <Text style={styles.summaryLabel}>Tax:</Text>
          <Text style={styles.summaryValue}>${tax.toFixed(2)}</Text>
        </View>
        <View style={[styles.summaryRow, styles.totalRow]}>
          <Text style={styles.totalLabel}>Total:</Text>
          <Text style={styles.totalValue}>${total.toFixed(2)}</Text>
        </View>
        <Pressable style={styles.checkoutButton}>
          <Text style={styles.checkoutText}>Proceed to Checkout</Text>
        </Pressable>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  itemsContainer: {
    flex: 1,
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    padding: 15,
    backgroundColor: '#f9f9f9',
  },
  cartItem: {
    flexDirection: 'row',
    padding: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
  itemImage: {
    width: 70,
    height: 70,
    backgroundColor: '#e0e0e0',
    borderRadius: 8,
  },
  itemDetails: {
    flex: 1,
    marginLeft: 15,
  },
  itemName: {
    fontSize: 16,
    fontWeight: '600',
  },
  itemPrice: {
    fontSize: 14,
    color: '#666',
    marginTop: 5,
  },
  quantityContainer: {
    flexDirection: 'row',
    marginTop: 5,
  },
  quantityLabel: {
    fontSize: 14,
    color: '#666',
  },
  quantityValue: {
    fontSize: 14,
    fontWeight: '600',
  },
  itemTotal: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#FF5722',
  },
  summaryContainer: {
    padding: 20,
    backgroundColor: '#f9f9f9',
    borderTopWidth: 2,
    borderTopColor: '#ddd',
  },
  summaryRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10,
  },
  summaryLabel: {
    fontSize: 16,
    color: '#666',
  },
  summaryValue: {
    fontSize: 16,
    fontWeight: '600',
  },
  totalRow: {
    marginTop: 10,
    paddingTop: 10,
    borderTopWidth: 1,
    borderTopColor: '#ddd',
  },
  totalLabel: {
    fontSize: 20,
    fontWeight: 'bold',
  },
  totalValue: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#FF5722',
  },
  checkoutButton: {
    backgroundColor: '#FF5722',
    padding: 15,
    borderRadius: 8,
    marginTop: 15,
    alignItems: 'center',
  },
  checkoutText: {
    color: 'white',
    fontSize: 18,
    fontWeight: 'bold',
  },
  dataBox: {
    backgroundColor: '#f0f0f0',
    margin: 15,
    padding: 15,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#ddd',
  },
  dataTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  dataScroll: {
    maxHeight: 150,
    backgroundColor: '#fff',
    padding: 10,
    borderRadius: 5,
  },
  dataText: {
    fontSize: 12,
    fontFamily: 'monospace',
  },
  buttonRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 10,
  },
  trackButton: {
    backgroundColor: '#2196F3',
    padding: 10,
    borderRadius: 5,
    flex: 1,
    marginHorizontal: 5,
    alignItems: 'center',
  },
  trackButtonText: {
    color: 'white',
    fontWeight: '600',
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
    backgroundColor: '#FF5722',
    padding: 10,
    marginHorizontal: 5,
    borderRadius: 5,
    alignItems: 'center',
  },
  navButtonText: {
    color: 'white',
    fontWeight: '600',
  },
});

export default CartScreen;
