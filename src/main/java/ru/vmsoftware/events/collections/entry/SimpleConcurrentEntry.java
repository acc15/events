package ru.vmsoftware.events.collections.entry;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-09
 */
public class SimpleConcurrentEntry<E extends ConcurrentEntry<E>> implements ConcurrentEntry<E> {

    final AtomicReference<E> prev = new AtomicReference<E>();
    final AtomicReference<E> next = new AtomicReference<E>();

    public E getPrev() {
        return prev.get();
    }

    public E getNext() {
        return next.get();
    }

    public boolean casNext(E expected, E update) {
        return next.compareAndSet(expected, update);
    }

    public void setPrev(E prev) {
        this.prev.lazySet(prev);
    }

    public void setNext(E next) {
        this.next.lazySet(next);
    }

}
