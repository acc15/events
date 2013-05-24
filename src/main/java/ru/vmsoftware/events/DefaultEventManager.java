package ru.vmsoftware.events;

import ru.vmsoftware.events.filters.Filter;
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
            public <T> void listen(Filter<T> filter, EventListener<T> listener) {
                entries.add(createEntry(filter, listener));
            }

            @Override
            public void mute(Object listener) {
                final Iterator<ListenerEntry> iter = entries.iterator();
                while (iter.hasNext()) {
                    final ListenerEntry e = iter.next();
                    final EventListener<?> l = e.listenerProvider.get();
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
    public <T> void listen(Filter<T> filter, EventListener<T> listener) {
        createEntry(filter, listener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean emit(Object event) {

        ensureNotNull("event can't be null", event);

        for (ListenerEntry e : list) {
            final Filter<Object> emitterFilter = (Filter<Object>) e.filterProvider.get();
            if (!emitterFilter.filter(event)) {
                continue;
            }

            final EventListener<Object> listener = (EventListener<Object>) e.listenerProvider.get();
            if (!listener.onEvent(event)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void mute(Object listener) {
        final Iterator<ListenerEntry> iter = list.iterator();
        while (iter.hasNext()) {
            final EventListener<?> l = iter.next().listenerProvider.get();
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

    <T> ListenerEntry createEntry(Filter<T> filter, EventListener<T> listener) {
        ensureNotNull("filter can't be null", filter);
        ensureNotNull("listener can't be null", listener);

        final ListenerEntry entry = new ListenerEntry(filter, listener);
        entry.initReferences(list.createEntryContainer(entry));

        list.add(entry);
        return entry;
    }

    static class ListenerEntry extends CustomWeakLinkedList.WeakEntry<ListenerEntry> implements ContainerManaged {
        ListenerEntry(
                Filter<?> filter,
                EventListener<?> listener) {
            this.filterProvider = new StrongProvider<Filter<?>>(filter);
            this.listenerProvider = new StrongProvider<EventListener<?>>(listener);
        }

        @Override
        public void initReferences(ReferenceContainer referenceContainer) {
            filterProvider = referenceContainer.manage(filterProvider, ManagementType.MANUAL);
            listenerProvider = referenceContainer.manage(listenerProvider, ManagementType.MANUAL);
        }

        Provider<Filter<?>> filterProvider;
        Provider<EventListener<?>> listenerProvider;
    }

    boolean matchListener(EventListener<?> l, Object listener) {
        return l.equals(listener) || l.isCounterpart(listener);
    }

    static void ensureNotNull(String description, Object object) {
        if (object == null) {
            throw new NullPointerException(description);
        }
    }

    CustomWeakLinkedList<ListenerEntry> list = new CustomWeakLinkedList<ListenerEntry>();
}
