package ru.vmsoftware.events.filters;

import ru.vmsoftware.events.references.CompositeObject;

import java.util.Collections;
import java.util.List;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
class OrFilter<T> implements Filter<T>, CompositeObject<Filter<T>> {

    OrFilter(final List<Filter<T>> filters) {
        this.filters = filters;
    }

    public boolean filter(T value) {
        for (final Filter<T> filter : filters) {
            if (filter.filter(value)) {
                return true;
            }
        }
        return false;
    }

    public List<Filter<T>> getUnderlyingObjects() {
        return Collections.unmodifiableList(filters);
    }

    private final List<Filter<T>> filters;
}

