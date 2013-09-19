package ru.vmsoftware.events.listeners.adapters;

import ru.vmsoftware.events.listeners.NoArgListener;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
public class NoArgListenerAdapter extends AbstractListenerAdapter<Object,Object,Object,NoArgListener> {

    public NoArgListenerAdapter(NoArgListener noArgListener) {
        super(noArgListener);
    }

    @Override
    protected boolean handleEvent(Object emitter, Object type, Object data, NoArgListener listener) {
        listener.onEvent();
        return true;
    }
}
