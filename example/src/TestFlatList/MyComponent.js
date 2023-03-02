import React from 'react';
import { StyleSheet } from 'react-native';
import { Text } from 'react-native';

const MyComponent = React.memo((props) => {
// const MyComponent = (props) => {
  const { title, id } = props;
  React.useEffect(() => {
    console.log('$$$ MyComponent: mounted - ',id);
    return () => {
      console.log('$$$ MyComponent: unMounted - ',id);
    };
  }, []);
  return <Text style={styles.main}>{title}</Text>;
// };
});

const styles = StyleSheet.create({
  main: {
    fontWeight: 'bold',
    color: 'red',
    borderWidth: 2,
    borderColor: 'blue',
    height: 200,
    textAlign: 'center',
    justifyContent: 'center',
  },
});

// export default React.memo(MyComponent);
export default MyComponent;
