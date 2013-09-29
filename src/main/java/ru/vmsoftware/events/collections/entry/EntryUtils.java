package ru.vmsoftware.events.collections.entry;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-09
 */
public class EntryUtils {

    public static boolean isMarker(Entry<?> e) {
        return e.getPrev() == e;
    }

    public static boolean isHead(Entry<?> e) {
        return e.getPrev() == null;
    }

    public static boolean isTail(Entry<?> e) {
        return e.getNext() == null;
    }

}
