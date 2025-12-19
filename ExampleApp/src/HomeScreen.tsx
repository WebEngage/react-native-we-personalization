import {useFocusEffect} from '@react-navigation/native';
import React, { useState } from 'react';
import {View, Text, StyleSheet, ScrollView, Pressable} from 'react-native';
import {WEInlineWidget} from 'react-native-we-personalization';
import WebEngage from 'react-native-webengage';
import {SCREEN_NAMES, BUTTON_LABELS} from './constants';

interface HomeScreenProps {
  navigation: any;
}

const HomeScreen: React.FC<HomeScreenProps> = ({navigation}) => {
  // ========== WebEngage Configuration ==========
  const SCREEN_NAME = 'screen1';
  const [isVisible1, setIsVisible1] = useState(false);
  const [isVisible2, setIsVisible2] = useState(false);
  const WIDGET_1 = {
    androidPropertyId: 'S1P1',
    iosPropertyId: 1,
    width: 350,
    height: 200,
  };

  const WIDGET_2 = {
    androidPropertyId: 'S1P2',
    iosPropertyId: 2,
    width: 300,
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

  // ========== UI Data ==========
  const categories = ['Electronics', 'Fashion', 'Home', 'Sports'];
  
  const products = [
    {id: 1, name: 'Wireless Headphones', price: '$99'},
    {id: 2, name: 'Smart Watch', price: '$199'},
    {id: 3, name: 'Laptop Stand', price: '$49'},
  ];

  // ========== Render ==========
  return (
    <View style={styles.wrapper}>
      <View style={styles.navButtons}>
        <Pressable style={styles.navButton} onPress={() => navigation.navigate(SCREEN_NAMES.ORDERS)}>
          <Text style={styles.navButtonText}>{BUTTON_LABELS.ORDERS}</Text>
        </Pressable>
        <Pressable style={styles.navButton} onPress={() => navigation.navigate(SCREEN_NAMES.CART)}>
          <Text style={styles.navButtonText}>{BUTTON_LABELS.CART}</Text>
        </Pressable>
      </View>
      <ScrollView style={styles.container}>
        {/* Banner Section */}
        <View style={styles.banner}>
          <Text style={styles.bannerText}>Welcome to Our Store</Text>
        </View>

      {/* Categories Section */}
      <Text style={styles.sectionTitle}>Categories</Text>
      <View style={styles.categoriesContainer}>
        {categories.map((cat, idx) => (
          <View key={idx} style={styles.categoryCard}>
            <Text style={styles.categoryText}>{cat}</Text>
          </View>
        ))}
      </View>

<View style={[{ height: isVisible1 ? WIDGET_2.height : 1, width: WIDGET_2.width }]}>

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
      </View>

      {/* Featured Products Section */}
      <Text style={styles.sectionTitle}>Featured Products</Text>
      {products.map(product => (
        <View key={product.id} style={styles.productCard}>
          <View style={styles.productImage} />
          <View style={styles.productInfo}>
            <Text style={styles.productName}>{product.name}</Text>
            <Text style={styles.productPrice}>{product.price}</Text>
          </View>
        </View>
      ))}


<View style={[{ height: isVisible2 ? WIDGET_2.height : 1, width: WIDGET_2.width }]}>
      {/* Widget 2 - After Products */}
      <WEInlineWidget
        style={{height: WIDGET_2.height, width: WIDGET_2.width}}
        screenName={SCREEN_NAME}
        androidPropertyId={WIDGET_2.androidPropertyId}
        iosPropertyId={WIDGET_2.iosPropertyId}
        onRendered={handleWidgetRendered}
        onDataReceived={handleWidgetDataReceived}
        onPlaceholderException={handleWidgetError}
      />
      </View>
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
    backgroundColor: '#f5f5f5',
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
    backgroundColor: '#4CAF50',
    padding: 10,
    marginHorizontal: 5,
    borderRadius: 5,
    alignItems: 'center',
  },
  navButtonText: {
    color: 'white',
    fontWeight: '600',
  },
  banner: {
    backgroundColor: '#4CAF50',
    padding: 40,
    alignItems: 'center',
  },
  bannerText: {
    color: 'white',
    fontSize: 24,
    fontWeight: 'bold',
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    margin: 15,
  },
  categoriesContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    padding: 10,
  },
  categoryCard: {
    backgroundColor: '#2196F3',
    padding: 20,
    margin: 5,
    borderRadius: 10,
    width: '45%',
  },
  categoryText: {
    color: 'white',
    fontSize: 16,
    textAlign: 'center',
  },
  productCard: {
    flexDirection: 'row',
    backgroundColor: 'white',
    margin: 10,
    padding: 15,
    borderRadius: 10,
  },
  productImage: {
    width: 80,
    height: 80,
    backgroundColor: '#ddd',
    borderRadius: 8,
  },
  productInfo: {
    marginLeft: 15,
    justifyContent: 'center',
  },
  productName: {
    fontSize: 16,
    fontWeight: '600',
  },
  productPrice: {
    fontSize: 18,
    color: '#4CAF50',
    marginTop: 5,
  },
});

export default HomeScreen;
