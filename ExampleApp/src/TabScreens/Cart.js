import React from 'react';
import {View, Text, StyleSheet, FlatList, TouchableOpacity} from 'react-native';
import {webengageInstance} from '../Utils/WebEngageManager';
import {useIsFocused} from '@react-navigation/native';
import WEInlineWidgetTemplate from './WEInlineWidgetTemplate';

const Cart = () => {
  const isFocused = useIsFocused();

  React.useEffect(() => {
    if (isFocused) {
      // The screen is focused, you can track your screen here
      console.log('Cart screen is focused');
      webengageInstance.screen('cart');
    }
  }, [isFocused]);

  const cartItems = [
    {id: '1', name: 'Product 1', price: 19.99},
    {id: '2', name: 'Product 2', price: 29.99},
    {id: '3', name: 'Product 3', price: 14.99},
  ];

  const total = cartItems.reduce((acc, item) => acc + item.price, 0);

  const handleDataReceived = data => {
    console.log('onDataReceived from Cart');
    // Your onDataReceived logic here
  };

  const handlePlaceholderException = data => {
    console.log('onPlaceholderException from Cart', data);
    // Your onPlaceholderException logic here
  };

  const handleRendered = data => {
    console.log('onRendered from Cart');
    // Your onRendered logic here
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Cart</Text>
      <FlatList
        data={cartItems}
        keyExtractor={item => item.id}
        renderItem={({item}) => (
          <View style={styles.cartItem}>
            <Text style={styles.itemName}>{item.name}</Text>
            <Text style={styles.itemPrice}>$ {item.price.toFixed(2)}</Text>
          </View>
        )}
      />
      <View style={styles.totalContainer}>
        <Text style={styles.totalText}>Total: $ {total.toFixed(2)}</Text>
        <TouchableOpacity style={styles.checkoutButton}>
          <Text style={styles.checkoutButtonText}>Checkout</Text>
        </TouchableOpacity>
      </View>
      <WEInlineWidgetTemplate
        screenName="cart"
        androidPropertyId={'cart1_scr1'}
        iosPropertyId={75992}
        style={styles.weINlineStyle}
        onDataReceived={handleDataReceived}
        onPlaceholderException={handlePlaceholderException}
        onRendered={handleRendered}
      />
      <Text style={styles.welcomeText}> Welcome to Cart Screen!</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
  },
  welcomeText: {
    fontSize: 24,
    fontWeight: 'bold',
    textAlign: 'center',
    marginTop: 20,
  },
  weINlineStyle: {
    height: 100,
    width: 350,
  },
  header: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  cartItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10,
  },
  itemName: {
    fontSize: 18,
  },
  itemPrice: {
    fontSize: 18,
    fontWeight: 'bold',
  },
  totalContainer: {
    marginTop: 20,
    alignItems: 'center',
  },
  totalText: {
    fontSize: 20,
    fontWeight: 'bold',
  },
  checkoutButton: {
    marginTop: 10,
    backgroundColor: '#007bff',
    paddingVertical: 10,
    paddingHorizontal: 20,
    borderRadius: 5,
  },
  checkoutButtonText: {
    color: 'white',
    fontSize: 18,
    fontWeight: 'bold',
  },
});

export default Cart;
