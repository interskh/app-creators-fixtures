package io.github.interskh.todo

import java.net.URLDecoder
import java.net.URLEncoder

data class Todo(
    val id: String,
    val text: String,
    val done: Boolean = false,
    val photo: String? = null,
)

fun List<Todo>.withAdded(id: String, text: String, photo: String? = null): List<Todo> {
    val trimmed = text.trim()
    return if (trimmed.isEmpty()) this else this + Todo(id, trimmed, photo = photo)
}

fun List<Todo>.withToggled(id: String): List<Todo> =
    map { if (it.id == id) it.copy(done = !it.done) else it }

fun List<Todo>.withoutId(id: String): List<Todo> = filter { it.id != id }

fun encodeTodos(todos: List<Todo>): String = todos.joinToString("\n") { todo ->
    listOf(todo.id, if (todo.done) "1" else "0", todo.photo.orEmpty(), todo.text)
        .joinToString("\t") { URLEncoder.encode(it, "UTF-8") }
}

fun decodeTodos(saved: String): List<Todo> = saved.lines().mapNotNull { line ->
    val fields = line.split("\t")
    if (fields.size != 4) return@mapNotNull null
    val (id, done, photo, text) = fields.map { URLDecoder.decode(it, "UTF-8") }
    Todo(id, text, done == "1", photo.ifEmpty { null })
}
