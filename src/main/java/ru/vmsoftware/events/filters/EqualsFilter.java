package ru.vmsoftware.events.filters;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
class EqualsFilter<T> implements Filter<T> {
    public EqualsFilter(T value) {
        this.value = value;
    }

    public boolean filter(T value) {
        return this.value.equals(value);
    }

    private T value;
}
