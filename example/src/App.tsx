import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import { WebengagePersonalizationView, multiply, add } from 'react-native-webengage-personalization';

export default function App() {
  const [result, setResult] = React.useState<number | undefined>();
  const [addition, setAddition] = React.useState<number | undefined>();
  React.useEffect(() => {
    multiply(6, 8).then(setResult);
    add(6, 8).then(setAddition);
  }, []);

  return (
    <View style={styles.container}>
      <WebengagePersonalizationView color="#32a852" style={styles.box} />
      <Text>Result: {result}</Text>
      <Text>Addition Result - {addition}</Text>
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
