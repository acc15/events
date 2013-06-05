package ru.vmsoftware.events.collections.concurrent;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * Implementation of non-blocking concurrent linked queue.
 * The key difference between {@link java.util.concurrent.ConcurrentLinkedQueue} is that this
 * collection exposes access to entries. Without entry access it isn't possible to implement
 * efficient weak concurrent queue.
 *
 * @author Vyacheslav Mayorov
 * @since 2013-02-06
 */
public class ConcurrentLinkedOpenQueue<T, E extends ConcurrentLinkedEntry<T,E>> implements Iterable<E> {

    private static final Logger log = Logger.getLogger(ConcurrentLinkedOpenQueue.class.getName());

    private AtomicReference<E> first = new AtomicReference<E>();
    private AtomicReference<E> last = new AtomicReference<E>();

    public void add(E newEntry) {
        E e = last.get();
        if (e == null) {
            log.fine("last is null; trying to casHead");
            if (first.compareAndSet(null, newEntry)) {
                log.info("casHead successful;");
                if (last.compareAndSet(null, newEntry)) {
                    log.info("casTail successful;");
                }
                return;
            }

            log.info("casHead fail. Picking element from head; It should be non-null");
            e = first.get();
        }
        while (!e.casNext(null, newEntry)) {
            e = e.getNext();
        }
        last.lazySet(newEntry);
    }

    public boolean isEmpty() {
        return first.get() == null;
    }

    public void clear() {
        first.lazySet(null);
        last.lazySet(null);
    }

    public Iterator<E> iterator() {
        // TODO implement..
        return null;
    }

    E first() {
        return first.get();
    }

    E last() {
        return last.get();
    }

}
