package ru.vmsoftware.events.linked;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-04-05
 */
public class CircularLinkedListTest extends AbstractLinkedListTest<CircularLinkedListTest.IntEntry> {

    private IntEntry[] testEntries = new IntEntry[] {new IntEntry(10), new IntEntry(15), new IntEntry(20)};

    @Override
    protected IntEntry[] getTestEntries() {
        return testEntries;
    }

    @Override
    protected CircularLinkedList<IntEntry> getList() {
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

    private CircularLinkedList<IntEntry> list = new CircularLinkedList<IntEntry>();

}
