package ru.vmsoftware.events.collections;

import org.junit.Test;
import ru.vmsoftware.events.TestUtils;
import ru.vmsoftware.events.providers.StrongProvider;

import java.util.Iterator;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * @author Vyacheslav Mayorov
 * @since 2013-05-05
 */
public class CustomWeakLinkedListTest extends AbstractLinkedListTest<CustomWeakLinkedListTest.TestWeakEntry> {

    private CustomWeakLinkedQueue<TestWeakEntry> list = new CustomWeakLinkedQueue<TestWeakEntry>();

    private TestWeakEntry createEntry(Object... refs) {
        final TestWeakEntry entry = new TestWeakEntry();
        final CustomWeakLinkedQueue.WeakEntryContainer container = list.createEntryContainer(entry);
        for (Object ref : refs) {
            container.manage(new StrongProvider<Object>(ref));
        }
        return entry;
    }

    static <E extends CustomWeakLinkedQueue.WeakEntry<E>> CustomWeakLinkedQueue<E>.WeakEntryContainer ref(
            CustomWeakLinkedQueue<E>.WeakEntryContainer container,
            Object ref) {
        container.manage(new StrongProvider<Object>(ref));
        return container;
    }

    private Object a = new Object();
    private Object b = new Object();
    private Object c = new Object();


    private TestWeakEntry[] testEntries = new TestWeakEntry[]{
            createEntry(a),
            createEntry(b),
            createEntry(c)
    };

    protected TestWeakEntry[] getTestEntries() {
        return testEntries;
    }

    protected CircularLinkedQueue<TestWeakEntry> getList() {
        return list;
    }

    @Test
    public void testListRemoveEntryIfAtLeastOneReferenceWasGC() throws Exception {

        Object x = new Object();
        Object y = new Object();
        Object z = new Object();

        list.add(createEntry(x, y, z));

        x = null;
        TestUtils.forceGC();
        assertThat(list.isEmpty()).isTrue();

    }

    @Test
    public void testListRemoveEntriesAutomaticallyWhenTheyGarbageCollected() throws Exception {
        getList().add(testEntries[0]);
        a = null;

        TestUtils.forceGC();
        assertThat(list.isEmpty()).isTrue();
    }

    @Test
    public void testIteratorShouldSkipGCEntries() throws Exception {
        getList().add(testEntries[0]);
        getList().add(testEntries[1]);
        getList().add(testEntries[2]);

        final Iterator<TestWeakEntry> iter = list.iterator();
        assertThat(iter.next()).isEqualTo(testEntries[0]);

        c = null;
        TestUtils.forceGC();
        TestUtils.assertIterator(iter, testEntries[1]);
    }

    static class TestWeakEntry extends CustomWeakLinkedQueue.WeakEntry<TestWeakEntry> {

    }

}
