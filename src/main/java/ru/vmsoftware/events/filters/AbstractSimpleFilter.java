package ru.vmsoftware.events.filters;

import java.util.Collections;
import java.util.List;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-25-05
 */
public abstract class AbstractSimpleFilter<T> implements Filter<T> {
    @Override
    public List<Filter<T>> getUnderlyingObjects() {
        return Collections.emptyList();
    }
}
