package ru.vmsoftware.events.collections;

import org.junit.Ignore;
import org.junit.Test;
import ru.vmsoftware.events.TestUtils;
import ru.vmsoftware.events.providers.Providers;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * @author Vyacheslav Mayorov
 * @since 2013-05-05
 */
public class CustomWeakLinkedListTest extends AbstractSimpleQueueTest<CustomWeakLinkedListTest.TestWeakEntry> {

    private CustomWeakOpenLinkedQueue<TestWeakEntry> queue = new CustomWeakOpenLinkedQueue<TestWeakEntry>();

    private TestWeakEntry createEntry(Object... refs) {
        final TestWeakEntry entry = new TestWeakEntry();
        final CustomWeakOpenLinkedQueue.WeakEntryManager container = queue.getReferenceManager(entry);
        for (Object ref : refs) {
            container.manage(Providers.strongRef(ref));
        }
        return entry;
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
        return queue;
    }

    @Test
    public void testListRemoveEntryIfAtLeastOneReferenceWasGC() throws Exception {

        Object x = new Object();
        Object y = new Object();
        Object z = new Object();

        queue.add(createEntry(x, y, z));

        x = null;
        TestUtils.forceGC();
        assertThat(queue.isEmpty()).isTrue();

    }

    @Test
    public void testListRemoveEntriesAutomaticallyWhenTheyGarbageCollected() throws Exception {
        getQueue().add(testEntries[0]);
        a = null;

        TestUtils.forceGC();
        assertThat(queue.isEmpty()).isTrue();
    }

    @Test
    public void testIteratorShouldSkipGCEntries() throws Exception {
        getQueue().add(testEntries[0]);
        getQueue().add(testEntries[1]);
        getQueue().add(testEntries[2]);

        final SimpleIterator<TestWeakEntry> iter = queue.iterator();
        assertThat(iter.next()).isEqualTo(testEntries[0]);

        c = null;
        TestUtils.forceGC();
        TestUtils.assertIterator(TestUtils.makeIterator(iter), testEntries[1]);
    }

    @Test @Ignore
    public void testIteratorShouldContinueIterationIfPointedEntryWasDeletedByGC() throws Exception {

        TestWeakEntry a = new TestWeakEntry();
        TestWeakEntry b = new TestWeakEntry();
        TestWeakEntry c = new TestWeakEntry();

        Object x = new Object();
        queue.getReferenceManager(b).manageObject(x);

        getQueue().add(a);
        getQueue().add(b);
        getQueue().add(c);

        final SimpleIterator<TestWeakEntry> iter = queue.iterator();
        iter.next();

        x = null;
        TestUtils.forceGC();

        // force cleanup of stales
        queue.isEmpty();
        assertThat(iter.next()).isSameAs(c);

    }

    static class TestWeakEntry extends CustomWeakOpenLinkedQueue.WeakEntry<TestWeakEntry> {

    }

}
