import * as React from 'react';
import { StyleSheet, View, Text } from 'react-native';
import SmsUserConsent from '../../src/index';

export default function App() {
  const [result, setResult] = React.useState<string | undefined>();

  React.useEffect(() => {
    SmsUserConsent.listenOTP().then((res) => setResult(res.receivedOtpMessage));
  }, []);

  return (
    <View style={styles.container}>
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
});
