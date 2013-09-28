package ru.vmsoftware.events.providers;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Utility class for creating {@link Provider} implementations.
 * @author Vyacheslav Mayorov
 * @since 2013-29-04
 */
public class Providers {

    // to prevent instantiation
    private Providers() {}

    /**
     * Creates provider which provides object which holds given {@link Reference}
     * @param reference reference to provide
     * @param <T> type of {@link Provider}
     * @return weak reference provider which provides given {@code obj}
     */
    public static <T> Provider<T> ref(Reference<T> reference) {
        return new ReferenceProvider<T>(reference);
    }

    /**
     * Creates provider which provides specified object and holds him by {@link WeakReference}
     * @param obj object to provide
     * @param <T> type of {@link Provider}
     * @return weak reference provider which provides given {@code obj}
     */
    public static <T> Provider<T> weakRef(T obj) {
        return ref(new WeakReference<T>(obj));
    }

    /**
     * Creates provider which provides specified object and holds him by strong reference
     * @param obj object to provide
     * @param <T> type of {@link Provider}
     * @return strong reference provider which provides given {@code obj}
     */
    public static <T> Provider<T> strongRef(T obj) {
        return new StrongProvider<T>(obj);
    }

}
