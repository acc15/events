package ru.vmsoftware.events.collections;

import ru.vmsoftware.events.collections.entry.ConcurrentEntry;
import ru.vmsoftware.events.collections.entry.EntryFactory;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-25-09
 */
public class ConcurrentOpenLinkedQueue<E extends ConcurrentEntry<E>> implements SimpleQueue<E> {

    private final EntryFactory<E> factory;
    private final E head, tail;

    public ConcurrentOpenLinkedQueue(EntryFactory<E> factory) {
        this.factory = factory;
        final E h = factory.createEntry(null, null);
        this.tail = factory.createEntry(h, null);
        this.head = h;
    }

    public boolean isEmpty() {
        // TODO implement..
        return false;
    }

    public void clear() {
        // TODO implement..

    }

    public void add(E value) {
        // TODO implement..

    }

    public boolean remove(E value) {
        // TODO implement..
        return false;
    }

    public SimpleIterator<E> iterator() {
        // TODO implement..
        return null;
    }
}
