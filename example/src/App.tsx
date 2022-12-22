import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import { WebengagePersonalizationView, multiply } from 'react-native-webengage-personalization';

export default function App() {
  const [result, setResult] = React.useState<number | undefined>();
  React.useEffect(() => {
    multiply(3, 7).then(setResult);
  }, []);

  return (
    <View style={styles.container}>
      <WebengagePersonalizationView color="#32a852" style={styles.box} />
      <Text>Result: {result}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
