package ru.vmsoftware.events.collections;

import org.apache.commons.lang.ObjectUtils;
import ru.vmsoftware.events.collections.entry.AbstractEntryFactory;
import ru.vmsoftware.events.collections.entry.SimpleConcurrentEntry;
import ru.vmsoftware.events.collections.entry.WeakContainer;

import java.lang.ref.Reference;
import java.util.Collections;
import java.util.List;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-09
 */
public class WeakQueue<T> implements SimpleQueue<T> {

    private static class WeakEntry<T> extends SimpleConcurrentEntry<WeakEntry<T>> implements WeakContainer<T> {

        private Reference<T> ref;

        private WeakEntry() {
        }

        public void setRef(Reference<T> ref) {
            this.ref = ref;
        }

        public List<Reference<T>> getReferences() {
            return ref != null ? Collections.singletonList(ref) : Collections.<Reference<T>>emptyList();
        }
    }

    private static class WeakEntryFactory<T> extends AbstractEntryFactory<WeakEntry<T>> {
        private static final WeakEntryFactory instance = new WeakEntryFactory();

        @SuppressWarnings("unchecked")
        public static <T> WeakEntryFactory<T> getInstance() {
            return (WeakEntryFactory<T>)instance;
        }

        private WeakEntryFactory() {
        }

        @Override
        protected WeakEntry<T> createEntry() {
            return new WeakEntry<T>();
        }
    }

    private WeakOpenQueue<T, WeakEntry<T>> queue = new WeakOpenQueue<T, WeakEntry<T>>(
            new ConcurrentOpenLinkedQueue<WeakEntry<T>>(WeakEntryFactory.<T>getInstance()));

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void clear() {
        queue.clear();
    }

    public void add(T value) {
        final WeakEntry<T> entry = new WeakEntry<T>();
        final Reference<T> ref = queue.createReference(entry, value);
        entry.setRef(ref);
        queue.add(entry);
    }

    public boolean remove(T value) {
        WeakEntry<T> e;
        final SimpleIterator<WeakEntry<T>> iter = queue.iterator();
        while ((e = iter.next()) != null) {
            if (ObjectUtils.equals(e.ref.get(), value)) {
                return true;
            }
        }
        return false;
    }

    public SimpleIterator<T> iterator() {
        return new SimpleIterator<T>() {

            private SimpleIterator<WeakEntry<T>> iter = queue.iterator();

            public T next() {
                final WeakEntry<T> e = iter.next();
                if (e == null) {
                    return null;
                }
                return e.ref.get();
            }

            public boolean remove() {
                return iter.remove();
            }
        };
    }

    @Override
    public String toString() {
        return SimpleQueueUtils.toString(this);
    }
}
