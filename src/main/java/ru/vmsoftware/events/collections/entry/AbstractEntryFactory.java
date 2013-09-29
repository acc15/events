package ru.vmsoftware.events.collections.entry;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-09
 */
public abstract class AbstractEntryFactory<E extends Entry<E>> implements EntryFactory<E> {

    protected abstract E createEntry();

    public final E createEntry(E prev, E next) {
        final E entry = createEntry();
        entry.setPrev(prev);
        entry.setNext(next);
        return entry;
    }

    public final E createMarker(E next) {
        final E entry = createEntry();
        entry.setPrev(entry);
        entry.setNext(next);
        return entry;
    }
}
