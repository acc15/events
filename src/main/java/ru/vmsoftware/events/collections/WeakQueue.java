package ru.vmsoftware.events.collections;

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
            return Collections.singletonList(ref);
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
        entry.setRef(queue.createReference(entry, value));
        queue.add(entry);
    }

    public boolean remove(T value) {
        // TODO implement..
        return false;
    }

    public SimpleIterator<T> iterator() {
        // TODO implement..
        return null;
    }
}
