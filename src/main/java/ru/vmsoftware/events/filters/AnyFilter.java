package ru.vmsoftware.events.filters;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
class AnyFilter<T> extends AbstractSimpleFilter<T> {
    public boolean filter(T value) {
        return true;
    }
}
