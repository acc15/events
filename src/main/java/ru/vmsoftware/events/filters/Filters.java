package ru.vmsoftware.events.filters;

import java.util.Arrays;
import java.util.List;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-08-05
 */
public class Filters {

    private static final AnyFilter<Object> ANY_FILTER_INSTANCE = new AnyFilter<Object>();

    @SuppressWarnings("unchecked")
    public static <T> Filter<T> any() {
        return (Filter<T>) ANY_FILTER_INSTANCE;
    }

    public static <T> Filter<T> sameInstance(T instance) {
        return new SameInstanceFilter<T>(instance);
    }

    public static <T> Filter<T> instanceOf(Class<?> type) {
        return new InstanceOfFilter<T>(type);
    }

    public static <T> Filter<T> and(Filter<T>... filters) {
        return and(Arrays.asList(filters));
    }

    public static <T> Filter<T> and(List<Filter<T>> filters) {
        if (filters.isEmpty()) {
            return any();
        }
        return filters.size() > 1 ? new AndFilter<T>(filters) : filters.get(0);
    }
}
