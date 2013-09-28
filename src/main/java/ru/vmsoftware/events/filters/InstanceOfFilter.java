package ru.vmsoftware.events.filters;

import ru.vmsoftware.events.references.ReferenceHolder;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-03-05
 */
class InstanceOfFilter<T> extends ReferenceHolder<Class<?>> implements Filter<T> {
    InstanceOfFilter(Class<?> clazz) {
        super(clazz);
    }

    public boolean filter(T value) {
        final Class<?> clazz = get();
        return clazz != null && clazz.isInstance(value);
    }
}
