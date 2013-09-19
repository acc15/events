package ru.vmsoftware.events.collections;

import ru.vmsoftware.events.providers.Provider;
import ru.vmsoftware.events.providers.Providers;
import ru.vmsoftware.events.references.AbstractReferenceManager;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-04-05
 */
public class CustomWeakOpenLinkedQueue<E extends CustomWeakOpenLinkedQueue.WeakEntry<E>>
        extends CircularOpenLinkedQueue<E> {

    public class WeakEntryManager extends AbstractReferenceManager {
        public WeakEntryManager(E entry) {
            this.entry = entry;
        }

        protected <T> Provider<T> manageObject(T obj) {
            return Providers.ref(entry.addRef(obj, staleRefs));
        }

        private E entry;
    }

    public WeakEntryManager createEntryContainer(E entry) {
        return new WeakEntryManager(entry);
    }

    public Iterator<E> iterator() {
        return new WeakIterator();
    }

    public boolean isEmpty() {
        cleanupStaleRefs();
        return super.isEmpty();
    }

    protected void insertBetween(E left, E right, E entry) {
        cleanupStaleRefs();
        super.insertBetween(left, right, entry);
    }

    protected void cleanupEntry(E entry) {
        super.cleanupEntry(entry);
        for (Ref<?, E> ref : entry.references) {
            ref.entry = null;
        }
        entry.references.clear();
    }

    public static class WeakEntry<E extends WeakEntry<E>> extends DoubleLinkedEntryBase<E> {

        @SuppressWarnings("unchecked")
        public <T> WeakReference<T> addRef(T t, ReferenceQueue<? super T> q) {
            final Ref<T, E> ref = new Ref<T, E>((E) this, t, q);
            references.add(ref);
            return ref;
        }

        @SuppressWarnings("unchecked")
        public <T> WeakReference<T> getRef() {
            return (WeakReference<T>) references.get(0);
        }

        List<Ref<?, E>> references = new ArrayList<Ref<?, E>>();
    }

    @SuppressWarnings("unchecked")
    void cleanupStaleRefs() {
        Ref<?, E> ref;
        while ((ref = (Ref<?, E>) staleRefs.poll()) != null) {
            if (ref.entry == null) {
                continue;
            }
            removeNotModify(ref.entry);
        }
    }

    static class Ref<T, E> extends WeakReference<T> {
        public Ref(E entry, T referent, ReferenceQueue<? super T> q) {
            super(referent, q);
            this.entry = entry;
        }

        private E entry;
    }

    class WeakIterator extends CircularListIterator {
        protected E lookupNext(E entry) {
            if (strongRefs == null) {
                strongRefs = new ArrayList<Object>();
            } else {
                strongRefs.clear();
            }

            E next = entry;
            e: while ((next = super.lookupNext(next)) != null) {
                for (Ref<?, E> ref : next.references) {
                    final Object o = ref.get();
                    if (o == null) {
                        strongRefs.clear();
                        continue e;
                    }
                    strongRefs.add(ref.get());
                }
                break;
            }
            return next;
        }

        /**
         * Holder of strong refs between {@link #hasNext()} and {@link #next()} calls
         */
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        private List<Object> strongRefs;
    }

    private ReferenceQueue<Object> staleRefs = new ReferenceQueue<Object>();
}
