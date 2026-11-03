package io.github.interskh.todo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import io.github.interskh.todo.ui.theme.TodoTheme
import java.io.File
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TodoScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

private const val PREFS = "todo"
private const val ITEMS = "items"

private fun photoFile(context: Context, name: String): File =
    File(File(context.filesDir, "photos").apply { mkdirs() }, name)

@Composable
fun TodoScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS, Context.MODE_PRIVATE) }
    var todos by remember { mutableStateOf(decodeTodos(prefs.getString(ITEMS, "").orEmpty())) }
    var text by remember { mutableStateOf("") }
    var pendingPhoto by remember { mutableStateOf<String?>(null) }

    fun update(next: List<Todo>) {
        todos = next
        prefs.edit().putString(ITEMS, encodeTodos(next)).apply()
    }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val name = UUID.randomUUID().toString() + ".jpg"
            photoFile(context, name).outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            pendingPhoto = name
        }
    }
    val requestCamera = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) takePicture.launch(null)
    }
    val pickPhoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            val name = UUID.randomUUID().toString() + ".jpg"
            context.contentResolver.openInputStream(uri)?.use { input ->
                photoFile(context, name).outputStream().use { input.copyTo(it) }
                pendingPhoto = name
            }
        }
    }

    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("New to-do") },
                modifier = Modifier.weight(1f),
            )
            Button(onClick = {
                val next = todos.withAdded(UUID.randomUUID().toString(), text, pendingPhoto)
                if (next !== todos) {
                    update(next)
                    text = ""
                    pendingPhoto = null
                }
            }) { Text("Add") }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
                if (granted) takePicture.launch(null) else requestCamera.launch(Manifest.permission.CAMERA)
            }) { Text("Take photo") }
            Button(onClick = {
                pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) { Text("Choose photo") }
            if (pendingPhoto != null) Text("Photo attached")
        }
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(todos, key = { it.id }) { todo ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = todo.done, onCheckedChange = { update(todos.withToggled(todo.id)) })
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = todo.text,
                            textDecoration = if (todo.done) TextDecoration.LineThrough else null,
                        )
                        todo.photo?.let { name ->
                            val bitmap = remember(name) {
                                val options = BitmapFactory.Options().apply { inSampleSize = 4 }
                                BitmapFactory.decodeFile(photoFile(context, name).path, options)?.asImageBitmap()
                            }
                            if (bitmap != null) {
                                Image(bitmap = bitmap, contentDescription = "Photo for ${todo.text}", modifier = Modifier.size(64.dp))
                            }
                        }
                    }
                    TextButton(onClick = {
                        todo.photo?.let { photoFile(context, it).delete() }
                        update(todos.withoutId(todo.id))
                    }) { Text("Delete") }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodoScreenPreview() {
    TodoTheme {
        TodoScreen()
    }
}
