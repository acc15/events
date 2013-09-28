package ru.vmsoftware.events.collections;

import org.junit.Test;
import ru.vmsoftware.events.TestUtils;
import ru.vmsoftware.events.providers.Providers;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * @author Vyacheslav Mayorov
 * @since 2013-05-05
 */
public class CustomWeakLinkedListTest extends AbstractSimpleQueueTest<CustomWeakLinkedListTest.TestWeakEntry> {

    private CustomWeakOpenLinkedQueue<TestWeakEntry> list = new CustomWeakOpenLinkedQueue<TestWeakEntry>();

    private TestWeakEntry createEntry(Object... refs) {
        final TestWeakEntry entry = new TestWeakEntry();
        final CustomWeakOpenLinkedQueue.WeakEntryManager container = list.getReferenceManager(entry);
        for (Object ref : refs) {
            container.manage(Providers.strongRef(ref));
        }
        return entry;
    }

    static <E extends CustomWeakOpenLinkedQueue.WeakEntry<E>> CustomWeakOpenLinkedQueue.WeakEntryManager ref(
            CustomWeakOpenLinkedQueue.WeakEntryManager container,
            Object ref) {
        container.manage(Providers.strongRef(ref));
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

    protected CircularOpenLinkedQueue<TestWeakEntry> getQueue() {
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
        getQueue().add(testEntries[0]);
        a = null;

        TestUtils.forceGC();
        assertThat(list.isEmpty()).isTrue();
    }

    @Test
    public void testIteratorShouldSkipGCEntries() throws Exception {
        getQueue().add(testEntries[0]);
        getQueue().add(testEntries[1]);
        getQueue().add(testEntries[2]);

        final SimpleIterator<TestWeakEntry> iter = list.iterator();
        assertThat(iter.next()).isEqualTo(testEntries[0]);

        c = null;
        TestUtils.forceGC();
        TestUtils.assertIterator(TestUtils.makeIterator(iter), testEntries[1]);
    }

    static class TestWeakEntry extends CustomWeakOpenLinkedQueue.WeakEntry<TestWeakEntry> {

    }

}
