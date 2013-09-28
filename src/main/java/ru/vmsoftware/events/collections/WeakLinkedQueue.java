package ru.vmsoftware.events.collections;

import org.apache.commons.lang.ObjectUtils;
import ru.vmsoftware.events.providers.Providers;

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
        list.getReferenceManager(entry).manage(Providers.strongRef(value));
        list.add(entry);
    }

    public boolean remove(final T value) {
        SimpleWeakEntry<T> entry;
        final SimpleIterator<SimpleWeakEntry<T>> iter = list.iterator();
        while ((entry = iter.next()) != null) {
            if (ObjectUtils.equals(value, entry.<T>getRef().get())) {
                iter.remove();
                return true;
            }
        }
        return false;
    }

    public SimpleIterator<T> iterator() {
        return new SimpleIterator<T>() {
            public T next() {
                final SimpleWeakEntry<T> entry = entryIterator.next();
                if (entry == null) {
                    return null;
                }
                return entry.<T>getRef().get();
            }

            public boolean remove() {
                return entryIterator.remove();
            }

            private SimpleIterator<SimpleWeakEntry<T>> entryIterator = list.iterator();
        };
    }

    private CustomWeakOpenLinkedQueue<SimpleWeakEntry<T>> list = new CustomWeakOpenLinkedQueue<SimpleWeakEntry<T>>();
}
