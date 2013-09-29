package ru.vmsoftware.events.collections;

import ru.vmsoftware.events.collections.entry.ConcurrentEntry;
import ru.vmsoftware.events.collections.entry.EntryFactory;
import ru.vmsoftware.events.collections.entry.EntryUtils;

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
        h.setNext(tail);
        this.head = h;
    }

    public boolean isEmpty() {
        return head.getNext() == tail;
    }

    public void clear() {
        head.setNext(tail);
        tail.setPrev(head);
    }

    public void add(E value) {
        value.setNext(tail);
        E prev;
        do {
            prev = tail.getPrev();
            value.setPrev(prev);
        } while (!prev.casNext(tail, value));
        tail.setPrev(value);
    }

    public boolean remove(E value) {
        E next, marker;
        do {
            next = value.getNext();
            if (EntryUtils.isMarker(next)) {
                // entry already deleted
                return false;
            }
            marker = factory.createMarker(next);
        } while (!value.casNext(next, marker));
        final E prev = value.getPrev();
        if (prev.casNext(value, next)) {
            next.setPrev(prev);
        }
        return true;
    }

    public SimpleIterator<E> iterator() {
        return new SimpleIterator<E>() {

            private E entry = head;

            public E next() {
                // TODO fix links prev/next linked to deleted (marked) nodes (this is potential memory leak)
                while ((entry = entry.getNext()) != null) {
                    if (EntryUtils.isTail(entry)) {
                        return null;
                    } else if (EntryUtils.isMarker(entry)) {
                        continue;
                    }

                    final E nextOver = entry.getNext();
                    if (EntryUtils.isMarker(nextOver)) {
                        entry = nextOver;
                        continue;
                    }
                    break;
                }
                return entry;
            }

            public boolean remove() {
                if (EntryUtils.isHead(entry)) {
                    throw new IllegalStateException("call next before calling remove");
                }
                return ConcurrentOpenLinkedQueue.this.remove(entry);
            }
        };
    }

    @Override
    public String toString() {
        return SimpleQueueUtils.toString(this);
    }
}
