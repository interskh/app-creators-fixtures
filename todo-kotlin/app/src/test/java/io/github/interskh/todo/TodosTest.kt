package io.github.interskh.todo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TodosTest {
    @Test
    fun addTrimsTextAndKeepsOrder() {
        val list = emptyList<Todo>().withAdded("a", "  first ").withAdded("b", "second", "b.jpg")
        assertEquals(listOf("first", "second"), list.map { it.text })
        assertEquals(listOf(null, "b.jpg"), list.map { it.photo })
        assertTrue(list.none { it.done })
    }

    @Test
    fun addIgnoresBlankText() {
        val list = listOf(Todo("a", "first"))
        assertEquals(list, list.withAdded("b", "   "))
    }

    @Test
    fun toggleFlipsOnlyThatItem() {
        val list = listOf(Todo("a", "first"), Todo("b", "second"))
        val toggled = list.withToggled("b")
        assertEquals(listOf(false, true), toggled.map { it.done })
        assertEquals(listOf(false, false), toggled.withToggled("b").map { it.done })
    }

    @Test
    fun deleteRemovesOnlyThatItem() {
        val list = listOf(Todo("a", "first"), Todo("b", "second"))
        assertEquals(listOf("b"), list.withoutId("a").map { it.id })
        assertEquals(list, list.withoutId("missing"))
    }

    @Test
    fun saveAndLoadRoundTrip() {
        val list = listOf(
            Todo("a", "buy milk\tand eggs\nthen go", done = true, photo = "a.jpg"),
            Todo("b", "plain"),
        )
        assertEquals(list, decodeTodos(encodeTodos(list)))
    }

    @Test
    fun loadSkipsEmptyAndBrokenLines() {
        assertEquals(emptyList<Todo>(), decodeTodos(""))
        assertEquals(emptyList<Todo>(), decodeTodos("not a saved item"))
        assertNull(decodeTodos(encodeTodos(listOf(Todo("a", "x")))).single().photo)
    }
}
