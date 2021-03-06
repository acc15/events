package ru.vmsoftware.events.listeners.adapters;

import ru.vmsoftware.events.listeners.*;
import ru.vmsoftware.events.references.ManagedObject;
import ru.vmsoftware.events.references.ManagementType;

/**
 * Listener which defines a set of methods to override
 * which is very convenient when declaring listener as anonymous class.
 * And since this class implements {@link ManagedObject} it allows to override management type
 * and make reference to this listener weak.
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
public class GenericListener<E,T,D> implements
        EventListener<E,T,D>,
        ManagedObject {

    public boolean handleEvent(E emitter, T type, D data) {
        onEvent(emitter, type, data);
        return true;
    }

    public void onEvent(E emitter, T type, D data) {
        onEvent(type, data);
    }

    public void onEvent(T type, D data) {
        onEvent(data);
    }

    public void onEvent(D data) {
        onEvent();
    }

    public void onEvent() {
    }

    public ManagementType getManagementType() {
        return ManagementType.MANUAL;
    }

    public boolean isCounterpart(Object obj) {
        return false;
    }
}
