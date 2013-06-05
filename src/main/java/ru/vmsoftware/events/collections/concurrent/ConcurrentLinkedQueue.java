package ru.vmsoftware.events.collections.concurrent;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Implementation of non-blocking concurrent linked queue.
 * The key difference between {@link java.util.concurrent.ConcurrentLinkedQueue} is that this
 * collection exposes access to entries. Without entry access it isn't possible to implement
 * efficient weak concurrent queue.
 *
 * @author Vyacheslav Mayorov
 * @since 2013-02-06
 */
public class ConcurrentLinkedQueue<T, E extends LinkedEntry<T,E>> implements Iterable<E> {

    public ConcurrentLinkedQueue(Class<E> entryClass) {
        updater = AtomicReferenceFieldUpdater.newUpdater(entryClass, entryClass, "next");
    }

    public void add(E entry) {
        E lastEntry;
        do {
            lastEntry = lastRef.get();
            if (lastEntry != null) {
                E nextEntry;
                do {
                    nextEntry = lastEntry.getNext();
                    entry.setNext(nextEntry);
                } while (!updater.compareAndSet(lastEntry, nextEntry, entry));
            } else {
                entry.setNext(entry);
            }
        } while (!lastRef.compareAndSet(lastEntry, entry));
    }

    public boolean isEmpty() {
        return lastRef.get() == null;
    }

    public void clear() {
        this.lastRef.lazySet(null);
    }

    public Iterator<E> iterator() {

    }

    private AtomicReferenceFieldUpdater<E,E> updater;
    private AtomicReference<E> lastRef = new AtomicReference<E>();
}
