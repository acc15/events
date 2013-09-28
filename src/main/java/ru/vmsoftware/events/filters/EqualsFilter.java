package ru.vmsoftware.events.filters;

import org.apache.commons.lang.ObjectUtils;
import ru.vmsoftware.events.references.ReferenceHolder;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
class EqualsFilter<T> extends ReferenceHolder<T> implements Filter<T> {
    EqualsFilter(T value) {
        super(value);
    }

    public boolean filter(T value) {
        return ObjectUtils.equals(get(), value);
    }
}
