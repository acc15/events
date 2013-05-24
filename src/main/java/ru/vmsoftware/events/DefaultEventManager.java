package ru.vmsoftware.events;

import ru.vmsoftware.events.filters.Filter;
import ru.vmsoftware.events.filters.Filters;
import ru.vmsoftware.events.linked.CustomWeakLinkedList;
import ru.vmsoftware.events.linked.WeakLinkedList;
import ru.vmsoftware.events.providers.Provider;
import ru.vmsoftware.events.providers.StrongProvider;
import ru.vmsoftware.events.references.ContainerManaged;
import ru.vmsoftware.events.references.ManagementType;
import ru.vmsoftware.events.references.ReferenceContainer;

import java.util.Iterator;

/** @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
class DefaultEventManager implements EventManager {

    @Override
    public Registrar createRegistrar() {
        return new Registrar() {
            @Override
            public void listen(Object emitter, Object event, EventListener<?, ?, ?> listener) {
                entries.add(createEntry(emitter, event, listener));
            }

            @Override
            public void mute(Object listener) {
                final Iterator<ListenerEntry> iter = entries.iterator();
                while (iter.hasNext()) {
                    final ListenerEntry e = iter.next();
                    final EventListener<?,?,?> l = e.listenerProvider.get();
                    if (l != null && matchListener(l, listener)) {
                        list.remove(e);
                        iter.remove();
                    }
                }
            }

            @Override
            public void cleanup() {
                for (ListenerEntry l: entries) {
                    list.remove(l);
                }
                entries.clear();
            }

            @Override
            public boolean isClean() {
                return entries.isEmpty();
            }

            private WeakLinkedList<ListenerEntry> entries = new WeakLinkedList<ListenerEntry>();
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean emit(Object emitter, Object event, Object data) {

        ensureNotNull("emitter can't be null", emitter);
        ensureNotNull("event can't be null", event);

        for (ListenerEntry e : list) {
            final Filter<Object> emitterFilter = (Filter<Object>) e.emitterFilterProvider.get();
            if (!emitterFilter.filter(emitter)) {
                continue;
            }

            final Filter<Object> typeFilter = (Filter<Object>) e.typeFilterProvider.get();
            if (!typeFilter.filter(event)) {
                continue;
            }

            final EventListener<Object, Object, Object> listener =
                    (EventListener<Object, Object, Object>) e.listenerProvider.get();
            if (!listener.onEvent(emitter, event, data)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void listen(Object emitter, Object event, EventListener<?, ?, ?> listener) {
        createEntry(emitter, event, listener);
    }

    ListenerEntry createEntry(Object emitter, Object event, EventListener<?, ?, ?> listener) {
        ensureNotNull("emitter can't be null", emitter);
        ensureNotNull("event can't be null", event);
        ensureNotNull("listener can't be null", listener);

        final Filter<?> emitterFilter = getFilter(emitter);
        final Filter<?> eventFilter = getFilter(event);

        final ListenerEntry entry = new ListenerEntry(emitterFilter, eventFilter, listener);
        entry.initReferences(list.createEntryContainer(entry));

        list.add(entry);
        return entry;
    }

    @Override
    public void mute(Object listener) {
        final Iterator<ListenerEntry> iter = list.iterator();
        while (iter.hasNext()) {
            final EventListener<?,?,?> l = iter.next().listenerProvider.get();
            if (matchListener(l, listener)) {
                iter.remove();
            }
        }
    }

    @Override
    public void cleanup() {
        list.clear();
    }

    @Override
    public boolean isClean() {
        return list.isEmpty();
    }

    static class ListenerEntry extends CustomWeakLinkedList.WeakEntry<ListenerEntry> implements ContainerManaged {
        ListenerEntry(
                Filter<?> emitterFilter,
                Filter<?> typeFilter,
                EventListener<?, ?, ?> listener) {
            this.emitterFilterProvider = new StrongProvider<Filter<?>>(emitterFilter);
            this.typeFilterProvider = new StrongProvider<Filter<?>>(typeFilter);
            this.listenerProvider = new StrongProvider<EventListener<?, ?, ?>>(listener);
        }

        @Override
        public void initReferences(ReferenceContainer referenceContainer) {
            emitterFilterProvider = referenceContainer.manage(emitterFilterProvider, ManagementType.MANUAL);
            typeFilterProvider = referenceContainer.manage(typeFilterProvider, ManagementType.MANUAL);
            listenerProvider = referenceContainer.manage(listenerProvider, ManagementType.MANUAL);
        }

        Provider<Filter<?>> emitterFilterProvider;
        Provider<Filter<?>> typeFilterProvider;
        Provider<EventListener<?, ?, ?>> listenerProvider;
    }

    boolean matchListener(EventListener<?, ?, ?> l, Object listener) {
        return l.equals(listener) || l.isCounterpart(listener);
    }

    Filter<?> getFilter(Object obj) {
        if (obj instanceof Filter<?>) {
            return (Filter<?>) obj;
        } else if (obj instanceof Class<?>) {
            return Filters.instanceOf((Class<?>)obj);
        } else {
            return Filters.sameInstance(obj);
        }
    }

    static void ensureNotNull(String description, Object object) {
        if (object == null) {
            throw new NullPointerException(description);
        }
    }

    CustomWeakLinkedList<ListenerEntry> list = new CustomWeakLinkedList<ListenerEntry>();
}
