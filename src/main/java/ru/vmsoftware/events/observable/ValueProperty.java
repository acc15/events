package ru.vmsoftware.events.observable;

import org.apache.commons.lang.ObjectUtils;
import ru.vmsoftware.events.Events;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
public class ValueProperty<T> implements ObservableProperty<T> {

    private T value;

    public ValueProperty() {
    }

    public ValueProperty(T initialValue) {
        this.value = initialValue;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        if (ObjectUtils.equals(this.value, value)) {
            return;
        }
        final T oldValue = this.value;
        this.value = value;
        Events.emit(this, ChangeEvent.class, new ChangeEvent<T>(oldValue, this.value));
    }
}
