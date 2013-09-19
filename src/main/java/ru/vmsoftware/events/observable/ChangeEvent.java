package ru.vmsoftware.events.observable;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
public class ChangeEvent<T> {

    private T oldValue;
    private T newValue;

    public ChangeEvent(T oldValue, T newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public T getOldValue() {
        return oldValue;
    }

    public T getNewValue() {
        return newValue;
    }
}
