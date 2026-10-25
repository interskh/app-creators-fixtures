import './style.css';
import { load, save, type Todo } from './store';

let items: Todo[] = load();

const app = document.querySelector<HTMLDivElement>('#app')!;
app.innerHTML = `
  <h1>Todo</h1>
  <form id="add">
    <input id="text" placeholder="What needs doing?" autocomplete="off" aria-label="New item" />
    <button type="submit">Add</button>
  </form>
  <ul id="list"></ul>
`;

const form = app.querySelector<HTMLFormElement>('#add')!;
const input = app.querySelector<HTMLInputElement>('#text')!;
const list = app.querySelector<HTMLUListElement>('#list')!;

function update(next: Todo[]): void {
  items = next;
  save(items);
  render();
}

function render(): void {
  list.replaceChildren(
    ...items.map((item) => {
      const li = document.createElement('li');
      li.classList.toggle('done', item.done);

      const label = document.createElement('label');
      const box = document.createElement('input');
      box.type = 'checkbox';
      box.checked = item.done;
      box.addEventListener('change', () =>
        update(items.map((t) => (t.id === item.id ? { ...t, done: !t.done } : t))),
      );
      label.append(box, document.createTextNode(item.text));

      const del = document.createElement('button');
      del.textContent = 'Delete';
      del.addEventListener('click', () => update(items.filter((t) => t.id !== item.id)));

      li.append(label, del);
      return li;
    }),
  );
}

form.addEventListener('submit', (event) => {
  event.preventDefault();
  const text = input.value.trim();
  if (!text) return;
  update([...items, { id: crypto.randomUUID(), text, done: false }]);
  input.value = '';
});

render();
