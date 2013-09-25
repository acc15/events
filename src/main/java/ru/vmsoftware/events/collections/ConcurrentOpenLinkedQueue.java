package ru.vmsoftware.events.collections;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-25-09
 */
public class ConcurrentOpenLinkedQueue<E extends ConcurrentOpenLinkedQueue.ConcurrentLinkedEntry<E>> implements SimpleQueue<E> {

    public static class ConcurrentLinkedEntry<E extends ConcurrentLinkedEntry<E>> {

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
    }


    private final AtomicReference<E> head = new AtomicReference<E>(), tail = new AtomicReference<E>();


    public boolean isEmpty() {
        return head.get() == null;
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

    public Iterator<E> iterator() {
        // TODO implement..
        return null;
    }



}
