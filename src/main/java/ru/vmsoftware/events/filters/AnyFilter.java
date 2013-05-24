package ru.vmsoftware.events.filters;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
public class AnyFilter<T> implements Filter<T> {
    private AnyFilter() {
    }

    @Override
    public boolean filter(T value) {
        return true;
    }

    @SuppressWarnings("unchecked")
    public static <T> AnyFilter<T> getInstance() {
        return (AnyFilter<T>) INSTANCE;
    }

    private static final AnyFilter<?> INSTANCE = new AnyFilter<Object>();
}
