package ru.vmsoftware.events.providers;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-04
 */
public class Providers {

    public static <T> Provider<T> ref(Reference<T> reference) {
        return new ReferenceProvider<T>(reference);
    }

    public static <T> Provider<T> weakRef(T obj) {
        return ref(new WeakReference<T>(obj));
    }

    public static <T> Provider<T> strongRef(T obj) {
        return new StrongProvider<T>(obj);
    }

}
