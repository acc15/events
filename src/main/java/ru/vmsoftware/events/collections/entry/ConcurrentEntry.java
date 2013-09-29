package ru.vmsoftware.events.collections.entry;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-09
 */
public interface ConcurrentEntry<E extends ConcurrentEntry<E>> extends Entry<E> {
    boolean casNext(E expected, E update);
}
