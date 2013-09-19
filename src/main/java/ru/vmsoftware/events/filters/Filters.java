package ru.vmsoftware.events.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-08-05
 */
public class Filters {

    private static final AnyFilter<Object> ANY_FILTER_INSTANCE = new AnyFilter<Object>();

    private static <T> List<Filter<T>> wrapWithEqualTo(T... values) {
        final List<Filter<T>> filterList = new ArrayList<Filter<T>>();
        for (T v: values) {
            filterList.add(equalTo(v));
        }
        return filterList;
    }

    @SuppressWarnings("unchecked")
    public static <T> Filter<T> any() {
        return (Filter<T>) ANY_FILTER_INSTANCE;
    }

    public static <T> Filter<T> equalTo(T value) {
        return new EqualsFilter<T>(value);
    }

    public static <T> Filter<T> toStringEqualTo(String value) {
        return new ToStringFilter<T>(value);
    }

    public static <T> Filter<T> sameInstance(T instance) {
        return new SameInstanceFilter<T>(instance);
    }

    public static <T> Filter<T> instanceOf(Class<?> type) {
        return new InstanceOfFilter<T>(type);
    }

    public static <T> Filter<T> and(T... values) {
        return and(wrapWithEqualTo(values));
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

    public static <T> Filter<T> or(T... values) {
        return or(wrapWithEqualTo(values));
    }

    public static <T> Filter<T> or(Filter<T>... filters) {
        return or(Arrays.asList(filters));
    }

    public static <T> Filter<T> or(List<Filter<T>> filters) {
        if (filters.isEmpty()) {
            return any();
        }
        return filters.size() > 1 ? new OrFilter<T>(filters) : filters.get(0);
    }
}
