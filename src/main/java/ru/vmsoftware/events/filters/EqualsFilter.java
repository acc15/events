package ru.vmsoftware.events.filters;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
public class EqualsFilter<T> extends AbstractSimpleFilter<T> {
    public EqualsFilter(T value) {
        this.value = value;
    }

    public boolean filter(T value) {
        return this.value.equals(value);
    }

    private T value;
}
