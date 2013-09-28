package ru.vmsoftware.events.filters;

import ru.vmsoftware.events.references.ReferenceHolder;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-03-05
 */
class SameInstanceFilter<T> extends ReferenceHolder<T> implements Filter<T> {
    SameInstanceFilter(T instance) {
        super(instance);
    }

    public boolean filter(T value) {
        return get() == value;
    }
}
