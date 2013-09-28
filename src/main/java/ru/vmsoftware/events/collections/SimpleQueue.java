package ru.vmsoftware.events.collections;

/**
 * Simple double linked queue interface
 * which doesn't expose access to underlying list entries.
 * It doesn't have {@code size()} and doesn't extend neither
 * {@link java.util.List} nor {@link java.util.Collection}
 * interfaces due to performance reasons
 *
 * @author Vyacheslav Mayorov
 * @since 2013-01-05
 */
public interface SimpleQueue<T>  {

    /**
     * Checks whether this queue is empty or not
     * @return {@code true} if list is empty, {@code false} otherwise
     */
    boolean isEmpty();

    /**
     * Clears queue
     */
    void clear();

    /**
     * Inserts {@code value} at last position
     * @param value value to insert
     */
    void add(T value);

    /**
     * Removes specified value from collection
     * @param value value to remove
     */
    boolean remove(T value);

    /**
     * Returns an {@link SimpleIterator}
     * @return iterator for iterating over collection items
     */
    SimpleIterator<T> iterator();

}
