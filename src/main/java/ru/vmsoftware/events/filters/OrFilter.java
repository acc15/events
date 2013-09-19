package ru.vmsoftware.events.filters;

import ru.vmsoftware.events.references.CompositeObject;

import java.util.List;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
class OrFilter<T> implements Filter<T>, CompositeObject<Filter<T>> {

    OrFilter(List<Filter<T>> filters) {
        this.filters = filters;
    }

    public boolean filter(T value) {
        for (Filter<T> filter : filters) {
            if (filter.filter(value)) {
                return true;
            }
        }
        return false;
    }

    public List<Filter<T>> getUnderlyingObjects() {
        return filters;
    }

    private List<Filter<T>> filters;
}

