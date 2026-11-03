import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:todo/main.dart';

void main() {
  setUp(() => SharedPreferences.setMockInitialValues({}));

  testWidgets('adds, completes, and deletes an item', (tester) async {
    await tester.pumpWidget(const TodoApp());

    await tester.enterText(find.byType(TextField), 'Buy milk');
    await tester.tap(find.text('Add'));
    await tester.pump();
    expect(find.text('Buy milk'), findsOneWidget);

    await tester.tap(find.text('Buy milk'));
    await tester.pump();
    final text = tester.widget<Text>(find.text('Buy milk'));
    expect(text.style?.decoration, TextDecoration.lineThrough);

    await tester.tap(find.byTooltip('Delete'));
    await tester.pump();
    expect(find.text('Buy milk'), findsNothing);
  });

  testWidgets('ignores empty text and keeps order', (tester) async {
    await tester.pumpWidget(const TodoApp());

    await tester.enterText(find.byType(TextField), '   ');
    await tester.tap(find.text('Add'));
    await tester.pump();
    expect(find.byType(ListTile), findsNothing);

    for (final item in ['First', 'Second']) {
      await tester.enterText(find.byType(TextField), item);
      await tester.tap(find.text('Add'));
      await tester.pump();
    }
    final titles = tester.widgetList<ListTile>(find.byType(ListTile)).map((t) => (t.title as Text).data);
    expect(titles, ['First', 'Second']);
  });

  testWidgets('loads saved items', (tester) async {
    SharedPreferences.setMockInitialValues({
      storageKey: '[{"id":"1","text":"Saved item","done":false,"photoPath":null}]',
    });
    await tester.pumpWidget(const TodoApp());
    await tester.pumpAndSettle();
    expect(find.text('Saved item'), findsOneWidget);
  });
}
