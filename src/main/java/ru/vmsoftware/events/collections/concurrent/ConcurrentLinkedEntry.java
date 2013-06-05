package ru.vmsoftware.events.collections.concurrent;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-02-06
 */
public interface ConcurrentLinkedEntry<T, E extends ConcurrentLinkedEntry<T,E>> {


    T getValue();
    void setValue(T value);

    E getNext();
    void setNext(E next);
    boolean casNext(E expected, E next);

}
