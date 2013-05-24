package ru.vmsoftware.events.filters;

import java.util.List;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-08-05
 */
class AndFilter<T> implements Filter<T> {

    AndFilter(List<Filter<T>> filters) {
        this.filters = filters;
    }

    @Override
    public boolean filter(T value) {
        for (Filter<T> filter: filters) {
            if (!filter.filter(value)) {
                return false;
            }
        }
        return true;
    }

    private List<Filter<T>> filters;
}
