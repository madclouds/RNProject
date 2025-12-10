import { View, Button, Platform, Text, StyleSheet } from 'react-native';
import React, { useEffect, useState } from 'react';
import DateTimePicker from '@react-native-community/datetimepicker';
import ApiHelper from './app/helpers/api/api_helper';

export default function App() {
  useEffect(() => {
    async function fetchData() {
      try {
        const result = await ApiHelper.getAccessToken();
        console.log(result);
      } catch (error) {
        // handle error
      }
    }

    fetchData();
  }, []);

  return (
    <View style={styles.container}>
      <Text>Hello, React Native!</Text>
      <MyDatePicker />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'white',
  },
});

const MyDatePicker = () => {
  const [date, setDate] = useState(new Date());
  const [show, setShow] = useState(false);

  const onChange = (_event, selectedDate) => {
    const currentDate = selectedDate || date;
    setShow(Platform.OS === 'ios'); // Hide picker on iOS after selection
    setDate(currentDate);
  };

  const showDatePicker = () => {
    setShow(true);
  };

  return (
    <View>
      <Button onPress={showDatePicker} title="Show Date Picker" />
      {show && (
        <DateTimePicker
          testID="dateTimePicker"
          value={date}
          mode="date"
          is24Hour={true}
          display="default"
          onChange={onChange}
        />
      )}
    </View>
  );
};
