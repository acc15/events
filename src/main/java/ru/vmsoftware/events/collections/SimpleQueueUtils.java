package ru.vmsoftware.events.collections;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-09
 */
public class SimpleQueueUtils {

    public static String toString(SimpleQueue<?> queue) {
        final StringBuilder builder = new StringBuilder();
        builder.append("{");
        final SimpleIterator<?> iter = queue.iterator();
        Object o;
        while ((o = iter.next()) != null) {
            if (builder.length() > 1) {
                builder.append("; ");
            }
            builder.append(o.toString());
        }
        builder.append("}");
        return builder.toString();
    }

}
