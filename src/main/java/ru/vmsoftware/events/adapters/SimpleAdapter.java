package ru.vmsoftware.events.adapters;

import ru.vmsoftware.events.EventListener;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-30-04
 */
public abstract class SimpleAdapter<T> implements EventListener<T> {

    @Override
    public boolean isCounterpart(Object obj) {
        return equals(obj);
    }
}
