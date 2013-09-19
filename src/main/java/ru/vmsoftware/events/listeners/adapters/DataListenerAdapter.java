package ru.vmsoftware.events.listeners.adapters;

import ru.vmsoftware.events.listeners.DataListener;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
public class DataListenerAdapter<D> extends AbstractListenerAdapter<Object,Object,D,DataListener<D>> {
    public DataListenerAdapter(DataListener<D> dataListener) {
        super(dataListener);
    }

    @Override
    protected boolean handleEvent(Object emitter, Object type, D data, DataListener<D> listener) {
        listener.onEvent(data);
        return true;
    }
}
