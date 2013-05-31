package ru.vmsoftware.events.filters;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
public class StringFilter<T> implements Filter<T> {

    public StringFilter(String value) {
        this.value = value;
    }

    public boolean filter(T value) {
        return this.value.equals(value.toString());
    }

    private String value;
}
