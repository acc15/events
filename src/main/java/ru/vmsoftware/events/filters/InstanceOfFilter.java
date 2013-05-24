package ru.vmsoftware.events.filters;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-03-05
 */
class InstanceOfFilter<T> implements Filter<T> {
    InstanceOfFilter(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean filter(T value) {
        return clazz.isInstance(value);
    }

    private Class<?> clazz;
}
