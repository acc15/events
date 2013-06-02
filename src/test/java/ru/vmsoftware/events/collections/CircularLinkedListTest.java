package ru.vmsoftware.events.collections;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-04-05
 */
public class CircularLinkedListTest extends AbstractLinkedListTest<CircularLinkedListTest.IntEntry> {

    private IntEntry[] testEntries = new IntEntry[]{new IntEntry(10), new IntEntry(15), new IntEntry(20)};

    protected IntEntry[] getTestEntries() {
        return testEntries;
    }

    protected CircularLinkedQueue<IntEntry> getList() {
        return list;
    }

    static class IntEntry extends DoubleLinkedEntryBase<IntEntry> {

        private IntEntry(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        private int value;
    }

    private CircularLinkedQueue<IntEntry> list = new CircularLinkedQueue<IntEntry>();

}
