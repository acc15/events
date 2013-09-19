package ru.vmsoftware.events.listeners.adapters;

import ru.vmsoftware.events.listeners.SimpleListener;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
public class SimpleListenerAdapter<E,T,D> extends AbstractListenerAdapter<E,T,D,SimpleListener<E,T,D>> {

    public SimpleListenerAdapter(SimpleListener<E, T, D> simpleListener) {
        super(simpleListener);
    }

    @Override
    protected boolean handleEvent(E emitter, T type, D data, SimpleListener<E, T, D> listener) {
        listener.onEvent(emitter, type, data);
        return true;
    }
}
