package ru.vmsoftware.events.collections;

import org.junit.Test;
import ru.vmsoftware.events.TestUtils;

import java.util.ConcurrentModificationException;

import static org.fest.assertions.api.Assertions.assertThat;
import static ru.vmsoftware.events.TestUtils.assertIterator;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-05-05
 */
@SuppressWarnings("unchecked")
public abstract class AbstractSimpleQueueTest<E extends DoubleLinkedEntry<E>> {

    protected abstract E[] getTestEntries();

    protected abstract SimpleQueue<E> getQueue();

    @Test
    public void testClearDoesntModifyListIfItWasEmpty() throws Exception {
        final SimpleIterator<E> iter = getQueue().iterator();
        getQueue().clear();
        assertThat(iter.next()).isNull();
    }

    @Test
    public void testClear() throws Exception {

        getQueue().add(getTestEntries()[0]);
        getQueue().add(getTestEntries()[1]);

        assertThat(getQueue().isEmpty()).isFalse();
        getQueue().clear();
        assertThat(getQueue().isEmpty()).isTrue();
        assertIterator(TestUtils.makeIterator(getQueue().iterator()));

    }

    @Test
    public void testAdd() throws Exception {
        getQueue().add(getTestEntries()[0]);
        getQueue().add(getTestEntries()[1]);
        assertThat(getQueue().isEmpty()).isFalse();
        assertIterator(TestUtils.makeIterator(getQueue().iterator()), getTestEntries()[0], getTestEntries()[1]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddThrowsExceptionIfEntryAlreadyAdded() throws Exception {
        getQueue().add(getTestEntries()[0]);
        getQueue().add(getTestEntries()[0]);
    }

    @Test
    public void testRemoveEntry() throws Exception {
        getQueue().add(getTestEntries()[0]);
        getQueue().add(getTestEntries()[1]);
        getQueue().remove(getTestEntries()[1]);
        assertIterator(TestUtils.makeIterator(getQueue().iterator()), getTestEntries()[0]);
    }

    @Test
    public void testRemoveEntryReturnsFalseIfEntryWasntAdded() throws Exception {
        assertThat(getQueue().remove(getTestEntries()[0])).isFalse();
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertThat(getQueue().isEmpty()).isTrue();
        getQueue().add(getTestEntries()[0]);
        assertThat(getQueue().isEmpty()).isFalse();
    }

    @Test
    public void testIteratorThrowsNoSuchElementException() throws Exception {
        assertThat(getQueue().iterator().next()).isNull();
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testIteratorThrowsConcurrentModificationException() throws Exception {
        getQueue().add(getTestEntries()[0]);
        final SimpleIterator<E> i = getQueue().iterator();
        getQueue().add(getTestEntries()[1]);
        i.next();
    }

    @Test(expected = IllegalStateException.class)
    public void testIteratorRemoveThrowsExceptionIfNextWasntCalled() throws Exception {
        getQueue().add(getTestEntries()[1]);
        getQueue().iterator().remove();
    }

    @Test
    public void testIteratorRemoveWorksCorrectly() throws Exception {
        getQueue().add(getTestEntries()[0]);
        getQueue().add(getTestEntries()[1]);
        getQueue().add(getTestEntries()[2]);

        final SimpleIterator<E> i = getQueue().iterator();
        i.next();
        i.next();
        assertThat(i.remove()).isTrue();
        i.next();

        assertIterator(TestUtils.makeIterator(getQueue().iterator()), getTestEntries()[0], getTestEntries()[2]);
    }
}
