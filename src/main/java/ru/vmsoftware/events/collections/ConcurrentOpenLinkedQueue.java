package ru.vmsoftware.events.collections;

import ru.vmsoftware.events.collections.entry.ConcurrentEntry;
import ru.vmsoftware.events.collections.entry.EntryFactory;
import ru.vmsoftware.events.collections.entry.EntryUtils;

/**
 * Marker idea taken from
 * <a href="http://www.java2s.com/Code/Java/Collections-Data-Structure/ConcurrentDoublyLinkedList.htm">here</a>.
 * Prev pointers may lag.
 * @author Vyacheslav Mayorov
 * @since 2013-25-09
 */
public class ConcurrentOpenLinkedQueue<E extends ConcurrentEntry<E>> implements SimpleQueue<E> {

    private final EntryFactory<E> factory;
    private final E head, tail;

    public ConcurrentOpenLinkedQueue(EntryFactory<E> factory) {
        this.factory = factory;
        final E h = factory.createEntry(null, null);
        final E t = factory.createEntry(h, null);
        h.setNext(t);
        this.head = h;
        this.tail = t;
    }

    public boolean isEmpty() {
        return head.getNext() == tail;
    }

    public void clear() {
        head.setNext(tail);
        tail.setPrev(head);
    }

    public void add(E entry) {
        entry.setNext(tail);
        E prev;
        do {
            prev = findPreviousNonDeletedEntry(tail);
        } while (!prev.casNext(tail, entry));
        entry.setPrev(prev);
        tail.setPrev(entry);
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

    private static <E extends ConcurrentEntry<E>> E findPreviousNonDeletedEntry(final E entry) {
        E prev = entry.getPrev();
        while (!EntryUtils.isHead(prev)) {
            final E found = findEntryPointingTo(prev, entry);
            if (found != null) {
                return found;
            }
            prev = prev.getPrev();
        }
        return prev;
    }

    private static <E extends ConcurrentEntry<E>> E findEntryPointingTo(final E start, final E target) {
        E entry = start;
        while (!EntryUtils.isTail(entry)) {
            final E next = findNextNonDeletedEntry(entry);
            if (next == target && !EntryUtils.isDeleted(entry)) {
                return entry;
            }
            entry = next;
        }
        return null;
    }

    private static <E extends ConcurrentEntry<E>> E findNextNonDeletedEntry(final E entry) {
        final E expectedNext = EntryUtils.nextNonMarker(entry);
        E next = expectedNext;
        while (!EntryUtils.isTail(next)) {
            final E nextNext = next.getNext();
            if (!EntryUtils.isMarker(nextNext)) {
                if (entry.casNext(expectedNext, next)) {
                    next.setPrev(entry);
                }
                return next;
            }
            next = nextNext.getNext();
        }
        return next;
    }

    private static <E extends ConcurrentEntry<E>> E findNextEntry(final E entry) {
        final E found = findNextNonDeletedEntry(entry);
        return EntryUtils.isTail(found) ? null : found;
    }

    private class ConcurrentIterator implements SimpleIterator<E> {

        private E entry;

        private ConcurrentIterator() {
            this.entry = head;
        }

        public E next() {
            if (entry == null) {
                throw new IllegalStateException("no more items available");
            }
            entry = findNextEntry(entry);
            return entry;
        }

        public boolean remove() {
            if (EntryUtils.isHead(entry)) {
                throw new IllegalStateException("call next before calling remove");
            }
            return ConcurrentOpenLinkedQueue.this.remove(entry);
        }
    }

    public SimpleIterator<E> iterator() {
        return new ConcurrentIterator();
    }

    @Override
    public String toString() {
        return SimpleQueueUtils.toString(this);
    }
}
