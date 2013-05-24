package ru.vmsoftware.events.linked;

/**
 * Simple double linked list interface
 * which doesn't expose access to underlying list entries.
 * It doesn't have {@code size()} and doesn't extend
 * {@link java.util.List} and {@link java.util.Collection}
 * interface due to performance reasons
 * @author Vyacheslav Mayorov
 * @since 2013-01-05
 */
public interface DoubleLinkedList<T> extends Iterable<T> {

    /**
     * Checks whether this list is empty or not
     * @return {@code true} if list is empty, {@code false} otherwise
     */
    boolean isEmpty();

    /**
     * Clears list
     */
    void clear();

    /**
     * Inserts {@code value} at first position
     * @param value value to insert
     */
    void add(T value);

}
