package ru.vmsoftware.events.observable;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
public interface ObservableProperty<T> {

    T get();
    void set(T value);

}
