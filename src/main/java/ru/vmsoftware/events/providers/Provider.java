package ru.vmsoftware.events.providers;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-04
 */
public interface Provider<T> {
    T get();
}
