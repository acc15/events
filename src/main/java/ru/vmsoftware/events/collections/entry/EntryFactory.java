package ru.vmsoftware.events.collections.entry;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-09
 */
public interface EntryFactory<E> {

    E createEntry(E prev, E next);
    E createMarker(E next);

}
