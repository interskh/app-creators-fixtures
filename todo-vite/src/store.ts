export interface Todo {
  id: string;
  text: string;
  done: boolean;
}

const KEY = 'todo-vite:items';

export function load(): Todo[] {
  try {
    const parsed: unknown = JSON.parse(localStorage.getItem(KEY) ?? '[]');
    return Array.isArray(parsed) ? (parsed as Todo[]) : [];
  } catch {
    return [];
  }
}

export function save(items: Todo[]): void {
  localStorage.setItem(KEY, JSON.stringify(items));
}
