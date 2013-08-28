package ru.vmsoftware.events.filters;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
class ToStringFilter<T> implements Filter<T> {

    public ToStringFilter(String value) {
        this.value = value;
    }

    public boolean filter(T value) {
        return this.value.equals(value.toString());
    }

    private String value;
}
