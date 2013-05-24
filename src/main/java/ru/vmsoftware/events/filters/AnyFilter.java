package ru.vmsoftware.events.filters;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
class AnyFilter<T> implements Filter<T> {
    @Override
    public boolean filter(T value) {
        return true;
    }
}
