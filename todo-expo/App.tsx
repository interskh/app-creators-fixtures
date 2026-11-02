import AsyncStorage from '@react-native-async-storage/async-storage';
import * as ImagePicker from 'expo-image-picker';
import { StatusBar } from 'expo-status-bar';
import { useEffect, useState } from 'react';
import {
  Button,
  FlatList,
  Image,
  Pressable,
  SafeAreaView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';

type Todo = { id: string; text: string; done: boolean; photoUri?: string };

const STORAGE_KEY = 'todos';

export default function App() {
  const [todos, setTodos] = useState<Todo[]>([]);
  const [loaded, setLoaded] = useState(false);
  const [text, setText] = useState('');
  const [photoUri, setPhotoUri] = useState<string | undefined>();

  useEffect(() => {
    AsyncStorage.getItem(STORAGE_KEY).then((raw) => {
      if (raw) setTodos(JSON.parse(raw));
      setLoaded(true);
    });
  }, []);

  useEffect(() => {
    if (loaded) AsyncStorage.setItem(STORAGE_KEY, JSON.stringify(todos));
  }, [todos, loaded]);

  async function pickPhoto() {
    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ['images'],
      quality: 0.7,
    });
    if (!result.canceled) setPhotoUri(result.assets[0].uri);
  }

  function add() {
    const trimmed = text.trim();
    if (!trimmed) return;
    setTodos([...todos, { id: String(Date.now()), text: trimmed, done: false, photoUri }]);
    setText('');
    setPhotoUri(undefined);
  }

  function toggle(id: string) {
    setTodos(todos.map((t) => (t.id === id ? { ...t, done: !t.done } : t)));
  }

  function remove(id: string) {
    setTodos(todos.filter((t) => t.id !== id));
  }

  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.title}>Todo</Text>
      <View style={styles.row}>
        <TextInput
          style={styles.input}
          value={text}
          onChangeText={setText}
          onSubmitEditing={add}
          placeholder="What needs doing?"
        />
        <Button title="Photo" onPress={pickPhoto} />
        <Button title="Add" onPress={add} />
      </View>
      {photoUri ? <Image source={{ uri: photoUri }} style={styles.preview} /> : null}
      <FlatList
        data={todos}
        keyExtractor={(t) => t.id}
        renderItem={({ item }) => (
          <View style={styles.row}>
            <Pressable style={styles.item} onPress={() => toggle(item.id)}>
              <Text style={item.done ? styles.done : undefined}>{item.text}</Text>
            </Pressable>
            {item.photoUri ? <Image source={{ uri: item.photoUri }} style={styles.thumb} /> : null}
            <Button title="Delete" onPress={() => remove(item.id)} />
          </View>
        )}
      />
      <StatusBar style="auto" />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#fff', padding: 24, paddingTop: 64 },
  title: { fontSize: 28, fontWeight: '600', marginBottom: 16 },
  row: { flexDirection: 'row', alignItems: 'center', gap: 8, marginBottom: 8 },
  input: { flex: 1, borderWidth: 1, borderColor: '#ccc', borderRadius: 6, padding: 8 },
  item: { flex: 1, paddingVertical: 8 },
  done: { textDecorationLine: 'line-through', color: '#888' },
  preview: { width: 64, height: 64, borderRadius: 6, marginBottom: 8 },
  thumb: { width: 40, height: 40, borderRadius: 4 },
});
