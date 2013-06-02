package ru.vmsoftware.events.collections;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-03-05
 */
public class CircularLinkedQueue<E extends DoubleLinkedEntry<E>> implements DoubleLinkedList<E> {

    public boolean isEmpty() {
        return last == null;
    }

    public void clear() {
        if (last == null) {
            return;
        }

        ++modCount;
        while (last.getNext() != null) {
            final E next = last.getNext();
            cleanupEntry(next);
            last = next;
        }
        last = null;
    }

    public final void add(E entry) {
        insertBetween(last, last != null ? last.getNext() : null, entry);
    }

    protected boolean removeNotModify(E entry) {
        // if next is null then entry is detached
        if (!isAdded(entry)) {
            return false;
        }

        if (entry.getNext() != entry) {
            entry.getNext().setPrevious(entry.getPrevious());
            entry.getPrevious().setNext(entry.getNext());
            if (last == entry) {
                last = entry.getPrevious();
            }
        } else {
            last = null;
        }
        cleanupEntry(entry);
        return true;
    }

    public boolean remove(E entry) {
        if (!removeNotModify(entry)) {
            return false;
        }
        ++modCount;
        return true;
    }

    public Iterator<E> iterator() {
        return new CircularListIterator();
    }

    protected void insertBetween(E left, E right, E entry) {
        if (isAdded(entry)) {
            throw new IllegalArgumentException("entry already added");
        }

        ++modCount;
        if (left != null) {
            left.setNext(entry);
        }
        if (right != null) {
            right.setPrevious(entry);
        }
        entry.setPrevious(left != null ? left : entry);
        entry.setNext(right != null ? right : entry);
        if (left == last) {
            last = entry;
        }
    }

    protected void cleanupEntry(E entry) {
        entry.setNext(null);
        entry.setPrevious(null);
    }

    protected class CircularListIterator implements Iterator<E> {
        public boolean hasNext() {
            return next != null;
        }

        public E next() {
            ensureNotModified();
            if (next == null) {
                throw new NoSuchElementException();
            }
            lastReturned = next;
            next = lookupNext(next);
            return lastReturned;
        }

        public void remove() {
            ensureNotModified();
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            ++expectedModCount;
            CircularLinkedQueue.this.remove(lastReturned);
            lastReturned = null;
        }

        protected E lookupNext(final E entry) {
            if (entry == null) {
                return last != null ? last.getNext() : null;
            }
            return entry != last ? entry.getNext() : null;
        }

        private void ensureNotModified() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }

        private int expectedModCount = modCount;
        private E lastReturned = null;
        private E next = lookupNext(null);
    }

    private boolean isAdded(E entry) {
        return entry.getNext() != null || entry.getPrevious() != null;
    }

    private volatile int modCount = 0;
    private E last;

}
