import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:shared_preferences/shared_preferences.dart';

const storageKey = 'todos';

void main() {
  runApp(const TodoApp());
}

class TodoApp extends StatelessWidget {
  const TodoApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Todo',
      theme: ThemeData(colorScheme: ColorScheme.fromSeed(seedColor: Colors.blue)),
      home: const TodoPage(),
    );
  }
}

class Todo {
  Todo(this.id, this.text, {this.done = false, this.photoPath});

  final String id;
  final String text;
  bool done;
  final String? photoPath;

  Map<String, dynamic> toJson() => {'id': id, 'text': text, 'done': done, 'photoPath': photoPath};

  factory Todo.fromJson(Map<String, dynamic> j) =>
      Todo(j['id'], j['text'], done: j['done'], photoPath: j['photoPath']);
}

class TodoPage extends StatefulWidget {
  const TodoPage({super.key});

  @override
  State<TodoPage> createState() => _TodoPageState();
}

class _TodoPageState extends State<TodoPage> {
  final _controller = TextEditingController();
  List<Todo> _todos = [];
  String? _photoPath;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final prefs = await SharedPreferences.getInstance();
    final raw = prefs.getString(storageKey);
    if (raw != null) {
      setState(() => _todos = (jsonDecode(raw) as List).map((j) => Todo.fromJson(j)).toList());
    }
  }

  Future<void> _save() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(storageKey, jsonEncode(_todos));
  }

  Future<void> _pickPhoto() async {
    final file = await ImagePicker().pickImage(source: ImageSource.gallery, imageQuality: 70);
    if (file != null) setState(() => _photoPath = file.path);
  }

  void _add() {
    final text = _controller.text.trim();
    if (text.isEmpty) return;
    setState(() {
      _todos.add(Todo(DateTime.now().microsecondsSinceEpoch.toString(), text, photoPath: _photoPath));
      _photoPath = null;
    });
    _controller.clear();
    _save();
  }

  void _toggle(Todo t) {
    setState(() => t.done = !t.done);
    _save();
  }

  void _remove(Todo t) {
    setState(() => _todos.remove(t));
    _save();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Todo')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _controller,
                    onSubmitted: (_) => _add(),
                    decoration: const InputDecoration(hintText: 'What needs doing?'),
                  ),
                ),
                IconButton(
                  tooltip: 'Photo',
                  icon: const Icon(Icons.photo),
                  onPressed: _pickPhoto,
                ),
                TextButton(onPressed: _add, child: const Text('Add')),
              ],
            ),
            if (_photoPath != null)
              Padding(
                padding: const EdgeInsets.only(top: 8),
                child: Image.file(File(_photoPath!), height: 64),
              ),
            Expanded(
              child: ListView(
                children: [
                  for (final t in _todos)
                    ListTile(
                      key: ValueKey(t.id),
                      leading: t.photoPath == null
                          ? null
                          : Image.file(File(t.photoPath!), width: 40, height: 40, fit: BoxFit.cover),
                      title: Text(
                        t.text,
                        style: t.done ? const TextStyle(decoration: TextDecoration.lineThrough) : null,
                      ),
                      onTap: () => _toggle(t),
                      trailing: IconButton(
                        tooltip: 'Delete',
                        icon: const Icon(Icons.delete),
                        onPressed: () => _remove(t),
                      ),
                    ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
