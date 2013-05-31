package ru.vmsoftware.events.filters;

import ru.vmsoftware.events.references.CompositeObject;

import java.util.List;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-08-05
 */
class AndFilter<T> implements Filter<T>, CompositeObject<Filter<T>> {

    AndFilter(List<Filter<T>> filters) {
        this.filters = filters;
    }

    public boolean filter(T value) {
        for (Filter<T> filter : filters) {
            if (!filter.filter(value)) {
                return false;
            }
        }
        return true;
    }

    public List<Filter<T>> getUnderlyingObjects() {
        return filters;
    }

    private List<Filter<T>> filters;
}
