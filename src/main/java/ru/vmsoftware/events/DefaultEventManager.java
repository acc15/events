package ru.vmsoftware.events;

import ru.vmsoftware.events.collections.CustomWeakLinkedQueue;
import ru.vmsoftware.events.filters.Filter;
import ru.vmsoftware.events.filters.Filters;
import ru.vmsoftware.events.collections.WeakLinkedList;
import ru.vmsoftware.events.providers.Provider;
import ru.vmsoftware.events.references.ContainerManaged;
import ru.vmsoftware.events.references.ManagementType;
import ru.vmsoftware.events.references.ReferenceContainer;

import java.util.Iterator;

import static ru.vmsoftware.events.providers.Providers.strongRef;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
class DefaultEventManager implements EventManager {

    public Registrar registrar() {
        return new Registrar() {

            public void listen(Object emitter, Object type, EventListener listener) {
                entries.add(createEntry(createFilterByObject(emitter), createFilterByObject(type), listener));
            }

            public void mute(Object listener) {
                final Iterator<ListenerEntry> iter = entries.iterator();
                while (iter.hasNext()) {
                    final ListenerEntry e = iter.next();
                    final EventListener l = e.listenerProvider.get();
                    if (l != null && matchListener(l, listener)) {
                        list.remove(e);
                        iter.remove();
                    }
                }
            }

            public void cleanup() {
                for (ListenerEntry l : entries) {
                    list.remove(l);
                }
                entries.clear();
            }

            public boolean isClean() {
                return entries.isEmpty();
            }

            private WeakLinkedList<ListenerEntry> entries = new WeakLinkedList<ListenerEntry>();
        };
    }

    public Emitter emitter(final Object emitter) {
        return new Emitter() {
            public boolean emit(Object type) {
                return DefaultEventManager.this.emit(emitter, type);
            }

            public boolean emit(Object type, Object data) {
                return DefaultEventManager.this.emit(emitter, type, data);
            }
        };
    }

    public void listen(Object emitter, Object type, EventListener listener) {
        createEntry(emitter, type, listener);
    }

    public boolean emit(Object emitter, Object type) {
        return emit(emitter, type, null);
    }

    @SuppressWarnings("unchecked")
    public boolean emit(Object emitter, Object type, Object data) {
        ensureNotNull("emitter can't be null", emitter);
        ensureNotNull("type can't be null", type);

        for (final ListenerEntry e : list) {

            final Filter emitterFilter = e.emitterFilterProvider.get();
            if (!emitterFilter.filter(emitter)) {
                continue;
            }

            final Filter typeFilter = e.typeFilterProvider.get();
            if (!typeFilter.filter(type)) {
                continue;
            }

            final EventListener listener = e.listenerProvider.get();
            if (!listener.onEvent(emitter, type, data)) {
                return false;
            }
        }
        return true;
    }

    public void mute(Object listener) {
        final Iterator<ListenerEntry> iter = list.iterator();
        while (iter.hasNext()) {
            final EventListener l = iter.next().listenerProvider.get();
            if (matchListener(l, listener)) {
                iter.remove();
            }
        }
    }

    public void cleanup() {
        list.clear();
    }

    public boolean isClean() {
        return list.isEmpty();
    }

    ListenerEntry createEntry(Object emitter, Object type, EventListener<?, ?, ?> listener) {
        ensureNotNull("emitter can't be null", emitter);
        ensureNotNull("type can't be null", type);
        ensureNotNull("listener can't be null", listener);

        final ListenerEntry entry = new ListenerEntry(
                createFilterByObject(emitter),
                createFilterByObject(type),
                listener);
        entry.initReferences(list.createEntryContainer(entry));

        list.add(entry);
        return entry;
    }

    static class ListenerEntry extends CustomWeakLinkedQueue.WeakEntry<ListenerEntry> implements ContainerManaged {
        ListenerEntry(Filter emitterFilter, Filter typeFilter, EventListener listener) {
            this.emitterFilterProvider = strongRef(emitterFilter);
            this.typeFilterProvider = strongRef(typeFilter);
            this.listenerProvider = strongRef(listener);
        }

        public void initReferences(ReferenceContainer referenceContainer) {
            emitterFilterProvider = referenceContainer.manage(emitterFilterProvider, ManagementType.MANUAL);
            typeFilterProvider = referenceContainer.manage(typeFilterProvider, ManagementType.MANUAL);
            listenerProvider = referenceContainer.manage(listenerProvider, ManagementType.MANUAL);
        }

        Provider<Filter> emitterFilterProvider;
        Provider<Filter> typeFilterProvider;
        Provider<EventListener> listenerProvider;
    }

    Filter<?> createFilterByObject(Object obj) {
        if (obj instanceof Filter) {
            return (Filter) obj;
        } else if (obj instanceof Class<?>) {
            return Filters.instanceOf((Class<?>) obj);
        } else {
            return Filters.sameInstance(obj);
        }
    }

    boolean matchListener(EventListener l, Object listener) {
        return l.equals(listener) || l.isCounterpart(listener);
    }

    static void ensureNotNull(String description, Object object) {
        if (object == null) {
            throw new NullPointerException(description);
        }
    }

    CustomWeakLinkedQueue<ListenerEntry> list = new CustomWeakLinkedQueue<ListenerEntry>();
}
