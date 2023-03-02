import React, { useCallback } from 'react';
import { FlatList, SafeAreaView, Text, View } from 'react-native';
import MyComponent from './MyComponent';

export default function Flatter() {
  const data = [];
  for (let i = 0; i < 300; i++) {
    data.push({ id: i, title: `${i}th item` });
  }

  const onRendered = (items) => {
    const { item, index } = items;
    // console.log('$$$ item id -> ', item.id);
    if (index === 1) {
      return (
        <View style={{ height: 200 }}>
          <MyComponent text={item?.title} id={item.id} />
        </View>
      );
    }

    if (index === 97) {
      return (
        <View style={{ height: 200 }}>
          <MyComponent text={item?.title} id={item.id} />
        </View>
      );
    }
    return (
      <View
        style={{
          height: 200,
          borderWidth: 1,
          margin: 10,
          justifyContent: 'center',
          alignItems: 'center',
        }}
      >
        <Text> {item.title} </Text>
      </View>
    );
  };

  return (
    <SafeAreaView>
      <FlatList
        data={data}
        initialNumToRender={0}
        keyExtractor={(item) => item.id}
        removeClippedSubviews={true}
        renderItem={onRendered}
      />
    </SafeAreaView>
  );
}
