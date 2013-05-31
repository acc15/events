package ru.vmsoftware.events.linked;

import org.junit.Test;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.fest.assertions.api.Assertions.assertThat;
import static ru.vmsoftware.events.TestUtils.assertIterator;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-05-05
 */
@SuppressWarnings("unchecked")
public abstract class AbstractLinkedListTest<E extends DoubleLinkedEntry<E>> {

    protected abstract E[] getTestEntries();

    protected abstract CircularLinkedList<E> getList();

    @Test(expected = NoSuchElementException.class)
    public void testClearDoesntModifyListIfItWasEmpty() throws Exception {
        final Iterator<E> iter = getList().iterator();
        getList().clear();
        iter.next();
    }

    @Test
    public void testClear() throws Exception {

        getList().add(getTestEntries()[0]);
        getList().add(getTestEntries()[1]);

        assertThat(getList().isEmpty()).isFalse();
        getList().clear();
        assertThat(getList().isEmpty()).isTrue();
        assertIterator(getList().iterator());

    }

    @Test
    public void testAdd() throws Exception {
        getList().add(getTestEntries()[0]);
        getList().add(getTestEntries()[1]);
        assertThat(getList().isEmpty()).isFalse();
        assertIterator(getList().iterator(), getTestEntries()[0], getTestEntries()[1]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddThrowsExceptionIfEntryAlreadyAdded() throws Exception {
        getList().add(getTestEntries()[0]);
        getList().add(getTestEntries()[0]);
    }

    @Test
    public void testRemoveEntry() throws Exception {
        getList().add(getTestEntries()[0]);
        getList().add(getTestEntries()[1]);
        getList().remove(getTestEntries()[1]);
        assertIterator(getList().iterator(), getTestEntries()[0]);
    }

    @Test
    public void testRemoveEntryReturnsFalseIfEntryWasntAdded() throws Exception {
        assertThat(getList().remove(getTestEntries()[0])).isFalse();
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertThat(getList().isEmpty()).isTrue();
        getList().add(getTestEntries()[0]);
        assertThat(getList().isEmpty()).isFalse();
    }

    @Test(expected = NoSuchElementException.class)
    public void testIteratorThrowsNoSuchElementException() throws Exception {
        getList().iterator().next();
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testIteratorThrowsConcurrentModificationException() throws Exception {
        getList().add(getTestEntries()[0]);
        final Iterator<E> i = getList().iterator();
        getList().add(getTestEntries()[1]);
        i.next();
    }

    @Test(expected = IllegalStateException.class)
    public void testIteratorRemoveThrowsExceptionIfNextWasntCalled() throws Exception {
        getList().add(getTestEntries()[1]);
        getList().iterator().remove();
    }

    @Test
    public void testIteratorRemoveWorksCorrectly() throws Exception {
        getList().add(getTestEntries()[0]);
        getList().add(getTestEntries()[1]);
        getList().add(getTestEntries()[2]);

        final Iterator<E> i = getList().iterator();
        i.next();
        i.next();
        i.remove();
        i.next();

        assertIterator(getList().iterator(), getTestEntries()[0], getTestEntries()[2]);
    }
}
