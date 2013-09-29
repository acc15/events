package ru.vmsoftware.events.collections.entry;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-09
 */
public class EntryUtils {

    public static boolean isDeleted(Entry<?> e) {
        return isMarker(e.getNext());
    }

    public static boolean isMarker(Entry<?> e) {
        return e.getPrev() == e;
    }

    public static <E extends Entry<E>> E nextNonMarker(E e) {
        final E next = e.getNext();
        if (isMarker(next)) {
            return next.getNext();
        } else {
            return next;
        }
    }

    public static boolean isHead(Entry<?> e) {
        return e.getPrev() == null;
    }

    public static boolean isTail(Entry<?> e) {
        return e.getNext() == null;
    }

}
