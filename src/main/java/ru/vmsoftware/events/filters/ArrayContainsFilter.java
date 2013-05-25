package ru.vmsoftware.events.filters;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
public class ArrayContainsFilter<T> extends AbstractSimpleFilter<T> {
    public ArrayContainsFilter(T... array) {
        this.array = array;
    }

    @Override
    public boolean filter(T value) {
        for (T val: array) {
            if (value.equals(val)) {
                return true;
            }
        }
        return false;
    }

    private T[] array;
}
