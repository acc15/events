package ru.vmsoftware.events;

import ru.vmsoftware.events.collections.*;
import ru.vmsoftware.events.filters.Filter;
import ru.vmsoftware.events.filters.Filters;
import ru.vmsoftware.events.listeners.EventListener;
import ru.vmsoftware.events.providers.Provider;
import ru.vmsoftware.events.references.ManagementType;
import ru.vmsoftware.events.references.ReferenceInitializer;
import ru.vmsoftware.events.references.ReferenceManager;

import static ru.vmsoftware.events.providers.Providers.strongRef;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
class DefaultEventManager extends AbstractRegistrar implements EventManager {

    public Registrar registrar() {
        return new AbstractRegistrar() {

            public void listen(Object emitter, Object type, EventListener<?,?,?> listener) {
                entries.add(createEntry(createFilterByObject(emitter), createFilterByObject(type), listener));
            }

            public void mute(Object listener) {
                ListenerEntry e;
                final SimpleIterator<ListenerEntry> iter = entries.iterator();
                while ((e = iter.next()) != null) {
                    final EventListener l = e.listenerProvider.get();
                    if (l != null && matchListener(l, listener)) {
                        list.remove(e);
                        iter.remove();
                    }
                }
            }

            public void cleanup() {
                ListenerEntry e;
                final SimpleIterator<ListenerEntry> iter = entries.iterator();
                while ((e = iter.next()) != null) {
                    list.remove(e);
                }
                entries.clear();
            }

            public boolean isClean() {
                return entries.isEmpty();
            }

            private WeakLinkedQueue<ListenerEntry> entries = new WeakLinkedQueue<ListenerEntry>();
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

    public void listen(Object emitter, Object type, EventListener<?,?,?> listener) {
        createEntry(emitter, type, listener);
    }

    public boolean emit(Object emitter, Object type) {
        return emit(emitter, type, null);
    }

    @SuppressWarnings("unchecked")
    public boolean emit(Object emitter, Object type, Object data) {
        ensureNotNull("emitter can't be null", emitter);
        ensureNotNull("type can't be null", type);

        ListenerEntry e;
        final SimpleIterator<ListenerEntry> iter = list.iterator();
        while ((e = iter.next()) != null) {

            final Filter emitterFilter = e.emitterFilterProvider.get();
            if (!emitterFilter.filter(emitter)) {
                continue;
            }

            final Filter typeFilter = e.typeFilterProvider.get();
            if (!typeFilter.filter(type)) {
                continue;
            }

            final EventListener listener = e.listenerProvider.get();
            if (!listener.handleEvent(emitter, type, data)) {
                return false;
            }
        }
        return true;
    }

    public void mute(Object listener) {
        ListenerEntry e;
        final SimpleIterator<ListenerEntry> iter = list.iterator();
        while ((e = iter.next()) != null) {
            final EventListener l = e.listenerProvider.get();
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

        final ReferenceManager referenceManager = list.getEntryManager(entry);
        try {
            entry.initReferences(referenceManager);
        } finally {
            referenceManager.finish();
        }

        list.add(entry);
        return entry;
    }

    static class ListenerEntry extends CustomWeakOpenLinkedQueue.WeakEntry<ListenerEntry> implements ReferenceInitializer {
        ListenerEntry(Filter emitterFilter, Filter typeFilter, EventListener listener) {
            this.emitterFilterProvider = strongRef(emitterFilter);
            this.typeFilterProvider = strongRef(typeFilter);
            this.listenerProvider = strongRef(listener);
        }

        public void initReferences(ReferenceManager referenceManager) {
            emitterFilterProvider = referenceManager.manage(emitterFilterProvider, ManagementType.MANUAL);
            typeFilterProvider = referenceManager.manage(typeFilterProvider, ManagementType.MANUAL);
            listenerProvider = referenceManager.manage(listenerProvider, ManagementType.MANUAL);
        }

        Provider<Filter> emitterFilterProvider;
        Provider<Filter> typeFilterProvider;
        Provider<EventListener> listenerProvider;
    }

    Filter<?> createFilterByObject(Object obj) {
        if (obj instanceof Filter) {
            return (Filter) obj;
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

    ConcurrentWeakQueueDecorator<ListenerEntry> list = new ConcurrentWeakQueueDecorator<ListenerEntry>(
        new CircularOpenLinkedQueue<ListenerEntry>()
    );
}
