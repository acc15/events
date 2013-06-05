package ru.vmsoftware.events.collections.concurrent;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-05-06
 */
public class ConcurrentLinkedEntryImpl<T> implements ConcurrentLinkedEntry<T, ConcurrentLinkedEntryImpl<T>> {

    public ConcurrentLinkedEntryImpl() {
    }

    public ConcurrentLinkedEntryImpl(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public ConcurrentLinkedEntryImpl<T> getNext() {
        return next.get();
    }

    public void setNext(ConcurrentLinkedEntryImpl<T> next) {
        this.next.set(next);
    }

    public boolean casNext(ConcurrentLinkedEntryImpl<T> expected, ConcurrentLinkedEntryImpl<T> next) {
        return this.next.compareAndSet(expected, next);
    }

    private volatile T value;
    private AtomicReference<ConcurrentLinkedEntryImpl<T>> next = new AtomicReference<ConcurrentLinkedEntryImpl<T>>();
}
