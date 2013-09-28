package ru.vmsoftware.events.collections;

import ru.vmsoftware.events.providers.Provider;
import ru.vmsoftware.events.providers.Providers;
import ru.vmsoftware.events.references.AbstractReferenceManager;
import ru.vmsoftware.events.references.ReferenceManager;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-09
 */
public class ConcurrentWeakQueueDecorator<E> implements SimpleQueue<E> {

    private SimpleQueue<E> queue;
    private final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<Object>();
    private final ConcurrentHashMap<E, List<Ref<?,E>>> entryReferences = new ConcurrentHashMap<E, List<Ref<?,E>>>();

    private static class Ref<T,E> extends WeakReference<T> {
        Ref(E entry, T referent, ReferenceQueue<? super T> queue) {
            super(referent, queue);
            this.entry = entry;
        }

        final E entry;
    }

    private class EntryReferenceManager extends AbstractReferenceManager {
        private final E entry;

        private EntryReferenceManager(E entry) {
            this.entry = entry;
        }

        @Override
        protected <T> Provider<T> manageObject(T obj) {
            if (refs == null) {
                throw new IllegalStateException("finish was called");
            }
            final Ref<T,E> ref = new Ref<T, E>(entry, obj, referenceQueue);
            refs.add(ref);
            return Providers.ref(ref);
        }

        public void finish() {
            entryReferences.put(entry, Collections.unmodifiableList(refs));
            refs = null;
        }

        private List<Ref<?,E>> refs = new ArrayList<Ref<?, E>>();
    }

    public ConcurrentWeakQueueDecorator(SimpleQueue<E> queue) {
        this.queue = queue;
    }

    public ReferenceManager getEntryManager(final E entry) {
        return new EntryReferenceManager(entry);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void clear() {
        entryReferences.clear();
        queue.clear();
    }

    public void add(E value) {
        cleanupStaleRefs();
        queue.add(value);
    }

    public boolean remove(E value) {
        cleanupStaleRefs();
        entryReferences.remove(value);
        return queue.remove(value);
    }

    public SimpleIterator<E> iterator() {
        return new SimpleIterator<E>() {

            private boolean catchEntryRefs(E entry) {
                strongRefs.clear();
                if (entry == null) {
                    return true;
                }

                final List<Ref<?,E>> refs = entryReferences.get(entry);
                if (refs == null) {
                    return true;
                }
                for (Ref<?,E> ref: refs) {
                    final Object obj = ref.get();
                    if (obj == null) {
                        return false;
                    }
                    strongRefs.add(obj);
                }
                return true;
            }

            public E next() {
                lastReturned = iter.next();
                while (!catchEntryRefs(lastReturned)) {
                    entryReferences.remove(lastReturned);
                    iter.remove();
                    lastReturned = iter.next();
                }
                return lastReturned;
            }

            public boolean remove() {
                if (lastReturned == null) {
                    throw new IllegalStateException();
                }
                entryReferences.remove(lastReturned);
                lastReturned = null;
                return iter.remove();
            }

            private E lastReturned = null;
            private final List<Object> strongRefs = new ArrayList<Object>();
            private final SimpleIterator<E> iter = queue.iterator();
        };
    }

    @SuppressWarnings("unchecked")
    private void cleanupStaleRefs() {
        Ref<?, E> ref;
        while ((ref = (Ref<?, E>) referenceQueue.poll()) != null) {
            final E entry = ref.entry;
            queue.remove(ref.entry);
            entryReferences.remove(entry);
        }
    }
}
