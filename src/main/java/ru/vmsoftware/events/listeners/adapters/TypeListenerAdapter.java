package ru.vmsoftware.events.listeners.adapters;

import ru.vmsoftware.events.listeners.TypeListener;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
public class TypeListenerAdapter<T,D> extends AbstractListenerAdapter<Object,T,D,TypeListener<T,D>> {

    public TypeListenerAdapter(TypeListener<T, D> typeListener) {
        super(typeListener);
    }

    @Override
    protected boolean handleEvent(Object emitter, T type, D data, TypeListener<T, D> listener) {
        listener.onEvent(type, data);
        return true;
    }
}
