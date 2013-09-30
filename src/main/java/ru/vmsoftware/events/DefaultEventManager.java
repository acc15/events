package ru.vmsoftware.events;

import ru.vmsoftware.events.collections.*;
import ru.vmsoftware.events.filters.Filter;
import ru.vmsoftware.events.filters.Filters;
import ru.vmsoftware.events.listeners.EventListener;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
class DefaultEventManager extends AbstractRegistrar implements EventManager {

    public Registrar registrar() {
        return new AbstractRegistrar() {

            public void listen(Object emitter, Object type, EventListener<?,?,?> listener) {
                entries.add(createEntry(emitter, type, listener));
            }

            public void mute(Object listener) {
                ListenerEntry e;
                final SimpleIterator<ListenerEntry> iter = entries.iterator();
                while ((e = iter.next()) != null) {
                    final EventListener l = e.getHeader().listenerProvider.get();
                    if (l != null && matchListener(l, listener)) {
                        queue.remove(e);
                        iter.remove();
                    }
                }
            }

            public void cleanup() {
                ListenerEntry e;
                final SimpleIterator<ListenerEntry> iter = entries.iterator();
                while ((e = iter.next()) != null) {
                    queue.remove(e);
                }
                entries.clear();
            }

            public boolean isClean() {
                return entries.isEmpty();
            }

            private WeakQueue<ListenerEntry> entries = new WeakQueue<ListenerEntry>();
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
        final SimpleIterator<ListenerEntry> iter = queue.iterator();
        while ((e = iter.next()) != null) {

            final Filter emitterFilter = e.getHeader().emitterFilterProvider.get();
            if (!emitterFilter.filter(emitter)) {
                continue;
            }

            final Filter typeFilter = e.getHeader().typeFilterProvider.get();
            if (!typeFilter.filter(type)) {
                continue;
            }

            final EventListener listener = e.getHeader().listenerProvider.get();
            if (!listener.handleEvent(emitter, type, data)) {
                return false;
            }
        }
        return true;
    }

    public void mute(Object listener) {
        ListenerEntry e;
        final SimpleIterator<ListenerEntry> iter = queue.iterator();
        while ((e = iter.next()) != null) {
            final EventListener l = e.getHeader().listenerProvider.get();
            if (matchListener(l, listener)) {
                iter.remove();
            }
        }
    }

    public void cleanup() {
        queue.clear();
    }

    public boolean isClean() {
        return queue.isEmpty();
    }

    ListenerEntry createEntry(Object emitter, Object type, EventListener<?, ?, ?> listener) {
        ensureNotNull("emitter can't be null", emitter);
        ensureNotNull("type can't be null", type);
        ensureNotNull("listener can't be null", listener);

        final ListenerEntry.Header header = new ListenerEntry.Header(
                createFilterByObject(emitter),
                createFilterByObject(type),
                listener);
        final ListenerEntry entry = new ListenerEntry(header);
        queue.add(entry);
        return entry;
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

    SimpleQueue<ListenerEntry> queue = new WeakOpenQueue<ListenerEntry>(
        new ConcurrentOpenLinkedQueue<ListenerEntry>(ListenerEntry.ListenerEntryFactory.getInstance())
    );
}
