package ru.vmsoftware.events.listeners.adapters;

import ru.vmsoftware.events.listeners.EventListener;
import ru.vmsoftware.events.providers.Provider;
import ru.vmsoftware.events.providers.Providers;
import ru.vmsoftware.events.references.ContainerManaged;
import ru.vmsoftware.events.references.ManagementType;
import ru.vmsoftware.events.references.ReferenceContainer;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
public abstract class AbstractListenerAdapter<E,T,D,L> implements EventListener<E,T,D>, ContainerManaged {

    private Provider<L> listener;

    protected AbstractListenerAdapter(L listener) {
        this.listener = Providers.strongRef(listener);
    }

    public void initReferences(ReferenceContainer referenceContainer) {
        this.listener = referenceContainer.manage(this.listener, ManagementType.MANUAL);
    }

    protected abstract boolean handleEvent(E emitter, T type, D data, L listener);

    public boolean onEvent(E emitter, T type, D data) {
        final L l = listener.get();
        return l == null || handleEvent(emitter, type, data, l);
    }

    public boolean isCounterpart(Object obj) {
        return obj.equals(listener.get());
    }
}
