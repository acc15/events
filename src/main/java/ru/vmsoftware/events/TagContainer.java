package ru.vmsoftware.events;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-08-05
 */
public interface TagContainer<T> {

    T getByTag(String tag);

}
