package ru.vmsoftware.events.filters;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-03-05
 */
class InstanceOfFilter<T> extends AbstractSimpleFilter<T> {
    InstanceOfFilter(Class<?> clazz) {
        this.clazz = clazz;
    }

    public boolean filter(T value) {
        return clazz.isInstance(value);
    }

    private Class<?> clazz;
}
