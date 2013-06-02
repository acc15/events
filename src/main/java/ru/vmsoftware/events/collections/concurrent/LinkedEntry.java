package ru.vmsoftware.events.collections.concurrent;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-02-06
 */
public interface LinkedEntry<T, E extends LinkedEntry<T,E>> {


    T getValue();

    E getNext();
    void setNext(E next);
}
