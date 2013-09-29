package ru.vmsoftware.events.collections.entry;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-09
 */
public interface Entry<E extends Entry<E>> {
    E getPrev();
    E getNext();
    void setPrev(E prev);
    void setNext(E next);
}
