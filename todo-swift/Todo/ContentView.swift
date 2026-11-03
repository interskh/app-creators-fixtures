import Photos
import PhotosUI
import SwiftUI

struct Todo: Codable, Identifiable, Equatable {
    var id = UUID()
    var text: String
    var done = false
    var photo: Data?
}

struct ContentView: View {
    @AppStorage("todos") private var stored = Data()
    @State private var todos: [Todo] = []
    @State private var text = ""
    @State private var photo: Data?
    @State private var showPicker = false
    @State private var selection: PhotosPickerItem?

    var body: some View {
        VStack(alignment: .leading) {
            Text("Todo").font(.largeTitle)
            HStack {
                TextField("What needs doing?", text: $text)
                    .textFieldStyle(.roundedBorder)
                    .onSubmit(add)
                Button("Photo", action: choosePhoto)
                Button("Add", action: add)
            }
            if let photo, let image = UIImage(data: photo) {
                Image(uiImage: image).resizable().scaledToFill().frame(width: 64, height: 64).clipped()
            }
            List {
                ForEach($todos) { $todo in
                    HStack {
                        Text(todo.text)
                            .strikethrough(todo.done)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .contentShape(Rectangle())
                            .onTapGesture { todo.done.toggle() }
                        if let data = todo.photo, let image = UIImage(data: data) {
                            Image(uiImage: image).resizable().scaledToFill().frame(width: 40, height: 40).clipped()
                        }
                    }
                }
                .onDelete { todos.remove(atOffsets: $0) }
            }
            .listStyle(.plain)
        }
        .padding()
        .photosPicker(isPresented: $showPicker, selection: $selection, matching: .images)
        .onAppear { todos = (try? JSONDecoder().decode([Todo].self, from: stored)) ?? [] }
        .onChange(of: todos) { save() }
        .onChange(of: selection) {
            Task { photo = try? await selection?.loadTransferable(type: Data.self) }
        }
    }

    func choosePhoto() {
        PHPhotoLibrary.requestAuthorization(for: .readWrite) { _ in
            DispatchQueue.main.async { showPicker = true }
        }
    }

    func add() {
        let trimmed = text.trimmingCharacters(in: .whitespaces)
        guard !trimmed.isEmpty else { return }
        todos.append(Todo(text: trimmed, photo: photo))
        text = ""
        photo = nil
        selection = nil
    }

    func save() {
        stored = (try? JSONEncoder().encode(todos)) ?? Data()
    }
}
