package ru.vmsoftware.events.collections;

import ru.vmsoftware.events.providers.Providers;

import java.util.Iterator;

/**
 * Simple adapter for {@link CustomWeakOpenLinkedQueue} which
 * hides entry operations providing simple object interface
 *
 * @author Vyacheslav Mayorov
 * @since 2013-01-05
 */
public class WeakLinkedQueue<T> implements SimpleQueue<T> {

    static class SimpleWeakEntry<T> extends CustomWeakOpenLinkedQueue.WeakEntry<SimpleWeakEntry<T>> {
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public void clear() {
        list.clear();
    }

    public void add(T value) {
        final SimpleWeakEntry<T> entry = new SimpleWeakEntry<T>();
        list.createEntryContainer(entry).manage(Providers.strongRef(value));
        list.add(entry);
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            public boolean hasNext() {
                return entryIterator.hasNext();
            }

            public T next() {
                return entryIterator.next().<T>getRef().get();
            }

            public void remove() {
                entryIterator.remove();
            }

            private Iterator<SimpleWeakEntry<T>> entryIterator = list.iterator();
        };
    }

    private CustomWeakOpenLinkedQueue<SimpleWeakEntry<T>> list = new CustomWeakOpenLinkedQueue<SimpleWeakEntry<T>>();
}
