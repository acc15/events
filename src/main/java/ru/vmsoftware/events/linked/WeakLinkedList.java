package ru.vmsoftware.events.linked;

import ru.vmsoftware.events.providers.StrongProvider;

import java.util.Iterator;

/**
 * Simple adapter for {@link CustomWeakLinkedList} which
 * hides entry operations providing simple object interface
 *
 * @author Vyacheslav Mayorov
 * @since 2013-01-05
 */
public class WeakLinkedList<T> implements DoubleLinkedList<T> {

    static class SimpleWeakEntry<T> extends CustomWeakLinkedList.WeakEntry<SimpleWeakEntry<T>> {
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public void clear() {
        list.clear();
    }

    public void add(T value) {
        final SimpleWeakEntry<T> entry = new SimpleWeakEntry<T>();
        list.createEntryContainer(entry).manage(new StrongProvider<T>(value));
        list.add(entry);
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            public boolean hasNext() {
                return entryIterator.hasNext();
            }

            public T next() {
                return entryIterator.next().<T>getRef(0).get();
            }

            public void remove() {
                entryIterator.remove();
            }

            private Iterator<SimpleWeakEntry<T>> entryIterator = list.iterator();
        };
    }

    private CustomWeakLinkedList<SimpleWeakEntry<T>> list = new CustomWeakLinkedList<SimpleWeakEntry<T>>();
}
