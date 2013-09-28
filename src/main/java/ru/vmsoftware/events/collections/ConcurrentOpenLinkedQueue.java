package ru.vmsoftware.events.collections;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-25-09
 */
public class ConcurrentOpenLinkedQueue<E extends ConcurrentOpenLinkedQueue.ConcurrentLinkedEntryBase<E>>
        implements SimpleQueue<E> {

    public static class ConcurrentLinkedEntryBase<E extends ConcurrentLinkedEntryBase<E>> {

        private final AtomicReference<E> prev = new AtomicReference<E>(), next = new AtomicReference<E>();

        public E getPrev() {
            return prev.get();
        }

        public void lazySetPrev(E previousEntry) {
            prev.lazySet(previousEntry);
        }

        public E getNext() {
            return next.get();
        }

        public boolean casNext(E expectedEntry, E nextEntry) {
            return next.compareAndSet(expectedEntry, nextEntry);
        }

        public void lazySetNext(E nextEntry) {
            next.lazySet(nextEntry);
        }
    }

    public static class ConcurrentLinkedEntry<T> extends ConcurrentLinkedEntryBase<ConcurrentLinkedEntry<T>> {

        public ConcurrentLinkedEntry(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        private final T value;
    }


    private final AtomicReference<E> head = new AtomicReference<E>(), tail = new AtomicReference<E>();


    public boolean isEmpty() {
        return tail.get() == null;
    }

    public void clear() {
        tail.lazySet(null);
        head.lazySet(null);
    }

    public void add(E value) {
        E last = tail.get();
        if (last == null) {
            if (tail.compareAndSet(null, value)) {
                head.lazySet(value);
                return;
            }

            // re-reading value if cas tail fails
            last = tail.get();
        }

        value.lazySetPrev(last);
        while (!last.casNext(null, value)) {
            last = last.getNext();
            value.lazySetPrev(last);
        }
        tail.lazySet(value);
    }

    public boolean remove(E value) {
        final E prev = value.getPrev();
        final E next = value.getNext();
        if (prev == null) {
            head.lazySet(next);
        } else {
            prev.lazySetNext(next);
        }
        if (next == null) {
            tail.lazySet(prev);
        } else {
            next.lazySetPrev(prev);
        }
        return true;
    }

    public SimpleIterator<E> iterator() {
        return new SimpleIterator<E>() {

            private E next = head.get();
            private E lastReturned;

            public E next() {
                lastReturned = next;
                next = next.getNext();
                return lastReturned;
            }

            public boolean remove() {
                if (lastReturned == null) {
                    throw new IllegalStateException("next wasn't called");
                }
                return ConcurrentOpenLinkedQueue.this.remove(lastReturned);
            }
        };
    }



}
