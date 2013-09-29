package ru.vmsoftware.events.collections;

import ru.vmsoftware.events.collections.entry.WeakContainer;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-09
 */
public class WeakOpenQueue<T,E extends WeakContainer<T>> implements SimpleQueue<E> {

    private static class Ref<T,E> extends WeakReference<T> {
        Ref(E entry, T referent, ReferenceQueue<? super T> queue) {
            super(referent, queue);
            this.entry = entry;
        }

        final E entry;
    }

    private final SimpleQueue<E> queue;
    private final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<Object>();

    public WeakOpenQueue(SimpleQueue<E> queue) {
        this.queue = queue;
    }

    public Reference<T> createReference(E entry, T obj) {
        return new Ref<T,E>(entry, obj, referenceQueue);
    }

    public boolean isEmpty() {
        cleanupStaleRefs();
        return queue.isEmpty();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public void clear() {
        while (referenceQueue.poll() != null);
        queue.clear();
    }

    public void add(E value) {
        cleanupStaleRefs();
        queue.add(value);
    }

    public boolean remove(E value) {
        cleanupStaleRefs();
        return queue.remove(value);
    }

    public SimpleIterator<E> iterator() {
        return new WeakIterator<E>(queue.iterator());
    }

    private static class WeakIterator<E extends WeakContainer<?>> implements SimpleIterator<E> {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        private final List<Object> strongRefs = new ArrayList<Object>();
        private final SimpleIterator<E> iter;

        private WeakIterator(SimpleIterator<E> iter) {
            this.iter = iter;
        }

        private boolean catchEntryRefs(E entry) {
            strongRefs.clear();
            if (entry == null) {
                return true;
            }

            final List<? extends Reference<?>> refs = entry.getReferences();
            if (refs == null) {
                return true;
            }
            for (Reference<?> ref: refs) {
                final Object obj = ref.get();
                if (obj == null) {
                    strongRefs.clear();
                    return false;
                }
                strongRefs.add(obj);
            }
            return true;
        }

        public E next() {
            E e = iter.next();
            while (!catchEntryRefs(e)) {
                iter.remove();
                e = iter.next();
            }
            return e;
        }

        public boolean remove() {
            return iter.remove();
        }
    }

    @SuppressWarnings("unchecked")
    private void cleanupStaleRefs() {
        Ref<?, E> ref;
        while ((ref = (Ref<?, E>) referenceQueue.poll()) != null) {
            queue.remove(ref.entry);
        }
    }

    @Override
    public String toString() {
        return queue.toString();
    }
}
