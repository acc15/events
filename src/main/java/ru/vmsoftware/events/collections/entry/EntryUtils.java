package ru.vmsoftware.events.collections.entry;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-09
 */
public class EntryUtils {

    public static boolean isMarker(ConcurrentEntry<?> e) {
        return e.getPrev() == e;
    }

    public static <E extends ConcurrentEntry<E>> E nextNonMarker(E e) {
        final E next = e.getNext();
        return isMarker(next) ? next.getNext() : next;
    }

    public static boolean isHead(Entry<?> e) {
        return e.getPrev() == null;
    }

    public static boolean isTail(Entry<?> e) {
        return e.getNext() == null;
    }

    public static boolean isDeleted(ConcurrentEntry<?> e) {
        return isMarker(e.getNext());
    }
}
