package ru.vmsoftware.events.collections;

import org.junit.Test;
import ru.vmsoftware.events.TestUtils;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * @author Vyacheslav Mayorov
 * @since 2013-01-05
 */
public class WeakLinkedListTest {

    @Test
    public void testListRemoveEntriesAutomaticallyWhenTheyGarbageCollected() throws Exception {

        Integer a = 1231;
        list.add(a);
        a = null;

        TestUtils.forceGC();

        assertThat(list.isEmpty()).isTrue();

    }

    @Test
    public void testIteratorShouldSkipGCEntries() throws Exception {

        Integer a = 523523;
        Integer b = 423423;
        Integer c = 32131;
        Integer d = 543543;
        list.add(a);
        list.add(b);
        list.add(c);
        list.add(d);

        final Iterator<Integer> iter = list.iterator();
        assertThat(iter.next()).isEqualTo(a);

        c = null;
        TestUtils.forceGC();

        assertThat(iter.next()).isEqualTo(b);
        assertThat(iter.next()).isEqualTo(d);
    }

    @Test
    public void testClear() throws Exception {

        list.add(10);
        list.add(15);
        list.add(20);

        assertThat(list.isEmpty()).isFalse();
        list.clear();
        assertThat(list.isEmpty()).isTrue();
        TestUtils.assertIterator(list.iterator());

    }

    @Test
    public void testAdd() throws Exception {
        list.add(50);
        list.add(1050);
        assertThat(list.isEmpty()).isFalse();
        TestUtils.assertIterator(list.iterator(), 50, 1050);
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertThat(list.isEmpty()).isTrue();
        list.add(21);
        assertThat(list.isEmpty()).isFalse();
    }

    @Test(expected = NoSuchElementException.class)
    public void testIteratorThrowsNoSuchElementException() throws Exception {
        list.iterator().next();
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testIteratorThrowsConcurrentModificationException() throws Exception {
        list.add(10);
        final Iterator<Integer> i = list.iterator();
        list.add(15);
        i.next();
    }

    @Test(expected = IllegalStateException.class)
    public void testIteratorRemoveThrowsExceptionIfNextWasntCalled() throws Exception {
        list.add(15);
        list.iterator().remove();
    }

    @Test
    public void testIteratorRemoveWorksCorrectly() throws Exception {
        list.add(10);
        list.add(15);
        list.add(20);

        final Iterator<Integer> i = list.iterator();
        assertThat(i.next()).isEqualTo(10);
        assertThat(i.next()).isEqualTo(15);
        i.remove();
        assertThat(i.next()).isEqualTo(20);

        TestUtils.assertIterator(list.iterator(), 10, 20);
    }

    private WeakLinkedList<Integer> list = new WeakLinkedList<Integer>();
}
