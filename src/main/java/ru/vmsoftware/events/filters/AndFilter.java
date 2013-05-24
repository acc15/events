package ru.vmsoftware.events.filters;

import java.util.Arrays;
import java.util.List;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-08-05
 */
public class AndFilter<T> implements Filter<T> {

    public AndFilter(List<Filter<T>> filters) {
        this.filters = filters;
    }

    public AndFilter(Filter<T> ...filters) {
        this.filters = Arrays.asList(filters);
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
