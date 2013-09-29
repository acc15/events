package ru.vmsoftware.events.collections;

import org.apache.commons.lang.ObjectUtils;
import ru.vmsoftware.events.collections.entry.AbstractEntryFactory;
import ru.vmsoftware.events.collections.entry.ConcurrentEntry;
import ru.vmsoftware.events.collections.entry.EntryUtils;
import ru.vmsoftware.events.collections.entry.SimpleConcurrentEntry;

/**
* @author Vyacheslav Mayorov
* @since 2013-29-09
*/
class TestConcurrentQueue<T> implements SimpleQueue<T> {

    private static class TestEntry<T> extends SimpleConcurrentEntry<TestEntry<T>> {

        TestEntry() {
            this.value = null;
        }

        TestEntry(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        private final T value;
    }

    private static class TestEntryFactory<T> extends AbstractEntryFactory<TestEntry<T>> {
        @Override
        protected TestEntry<T> createEntry(boolean marker) {
            return new TestEntry<T>();
        }

        @Override
        public TestEntry<T> createEntry(TestEntry<T> prev, TestEntry<T> next) {
            final TestEntry<T> e = super.createEntry(prev, next);
            if (prev == null && next == null) {
                head = e;
            }
            return e;
        }

        private TestEntry<T> head;
    }

    private TestEntryFactory<T> entryFactory = new TestEntryFactory<T>();

    private ConcurrentOpenLinkedQueue<TestEntry<T>> entryQueue =
            new ConcurrentOpenLinkedQueue<TestEntry<T>>(entryFactory);

    public boolean isEmpty() {
        return entryQueue.isEmpty();
    }

    public void clear() {
        entryQueue.clear();
    }

    public void add(T value) {
        entryQueue.add(new TestEntry<T>(value));
    }

    public boolean remove(T value) {
        TestEntry<T> e;
        final SimpleIterator<TestEntry<T>> iter = entryQueue.iterator();
        while ((e = iter.next()) != null) {
            if (ObjectUtils.equals(value, e.getValue())) {
                iter.remove();
                return true;
            }
        }
        return false;
    }

    private class TestIterator implements SimpleIterator<T> {
        public T next() {
            final TestEntry<T> entry = iter.next();
            if (entry == null) {
                return null;
            }
            return entry.getValue();
        }

        public boolean remove() {
            return iter.remove();
        }

        private SimpleIterator<TestEntry<T>> iter = entryQueue.iterator();

    }

    public SimpleIterator<T> iterator() {
        return new TestIterator();
    }

    public static class ListReport {
        int totalNextNodes = 0;
        int totalNextMarkers = 0;
        int totalPrevNodes = 0;
        int totalPrevMarkers = 0;
        int totalPrevIncorrectLinks = 0;

        @Override
        public String toString() {
            return "{Total forward nodes: " + totalNextNodes + "; Total back nodes: " + totalPrevNodes +
                    "; Total next markers: " + totalNextMarkers + "; Total back markers: " + totalPrevMarkers +
                    "; Total incorrect links: " + totalPrevIncorrectLinks + "}";
        }
    }

    public static <E extends ConcurrentEntry<E>> ListReport reportDeadNodes(E e) {
        ListReport report = new ListReport();
        for (;;) {
            final E next = e.getNext();
            if (next == null) {
                break;
            }
            if (!EntryUtils.isHead(e)) {
                if (EntryUtils.isMarker(e)) {
                    ++report.totalNextMarkers;
                } else {
                    ++report.totalNextNodes;
                }
            }
            e = next;
        }
        for (;;) {
            final E prev = e.getPrev();
            if (prev == null) {
                break;
            }
            if (!EntryUtils.isTail(e)) {
                ++report.totalPrevNodes;
            }
            final E prevNext = prev.getNext();
            if (prevNext != e) {
                if (EntryUtils.isMarker(prevNext)) {
                    ++report.totalPrevMarkers;
                } else {
                    ++report.totalPrevIncorrectLinks;
                }
            }
            e = prev;
        }
        return report;
    }

    public TestEntry<T> getHead() {
        return entryFactory.head;
    }



}
