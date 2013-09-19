package ru.vmsoftware.events;

import ru.vmsoftware.events.listeners.*;
import ru.vmsoftware.events.listeners.adapters.DataListenerAdapter;
import ru.vmsoftware.events.listeners.adapters.NoArgListenerAdapter;
import ru.vmsoftware.events.listeners.adapters.SimpleListenerAdapter;
import ru.vmsoftware.events.listeners.adapters.TypeListenerAdapter;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
@SuppressWarnings("unchecked")
public abstract class AbstractRegistrar implements Registrar {

    public void listen(Object emitter, Object type, SimpleListener<?,?,?> listener) {
        this.listen(emitter, type, new SimpleListenerAdapter<Object,Object,Object>(
                (SimpleListener<Object,Object,Object>)listener));
    }

    public void listen(Object emitter, Object type, TypeListener<?,?> listener) {
        listen(emitter, type, new TypeListenerAdapter<Object,Object>((TypeListener<Object,Object>)listener));
    }

    public void listen(Object emitter, Object type, DataListener<?> listener) {
        listen(emitter, type, new DataListenerAdapter<Object>((DataListener<Object>)listener));
    }

    public void listen(Object emitter, Object type, NoArgListener listener) {
        listen(emitter, type, new NoArgListenerAdapter(listener));
    }

}
