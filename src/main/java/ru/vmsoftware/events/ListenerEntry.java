package ru.vmsoftware.events;

import ru.vmsoftware.events.collections.entry.AbstractEntryFactory;
import ru.vmsoftware.events.collections.entry.SimpleConcurrentEntry;
import ru.vmsoftware.events.collections.entry.WeakContainer;
import ru.vmsoftware.events.filters.Filter;
import ru.vmsoftware.events.listeners.EventListener;
import ru.vmsoftware.events.providers.Provider;
import ru.vmsoftware.events.providers.Providers;
import ru.vmsoftware.events.references.ManagementType;
import ru.vmsoftware.events.references.ReferenceInitializer;
import ru.vmsoftware.events.references.ReferenceManager;

import java.lang.ref.Reference;
import java.util.List;

/**
* @author Vyacheslav Mayorov
* @since 2013-29-09
*/
class ListenerEntry extends SimpleConcurrentEntry<ListenerEntry> implements WeakContainer<Object> {


    static class ListenerEntryFactory extends AbstractEntryFactory<ListenerEntry> {

        private static final ListenerEntryFactory instance = new ListenerEntryFactory();

        public static ListenerEntryFactory getInstance() {
            return instance;
        }

        private ListenerEntryFactory() {
        }

        @Override
        protected ListenerEntry createEntry(boolean marker) {
            return new ListenerEntry();
        }
    }

    public static class Header implements ReferenceInitializer {
        Provider<Filter> emitterFilterProvider;
        Provider<Filter> typeFilterProvider;
        Provider<EventListener> listenerProvider;
        List<Reference<Object>> references;

        public Header(Filter emitterFilter,
                      Filter typeFilter,
                      EventListener listener) {
            this.emitterFilterProvider = Providers.strongRef(emitterFilter);
            this.typeFilterProvider = Providers.strongRef(typeFilter);
            this.listenerProvider = Providers.strongRef(listener);
        }

        public void initReferences(ReferenceManager referenceManager) {
            this.emitterFilterProvider = referenceManager.manage(emitterFilterProvider, ManagementType.MANUAL);
            this.typeFilterProvider = referenceManager.manage(typeFilterProvider, ManagementType.MANUAL);
            this.listenerProvider = referenceManager.manage(listenerProvider, ManagementType.MANUAL);
        }

        public void setReferences(List<Reference<Object>> refs) {
            this.references = refs;
        }
    }

    final Header header;

    ListenerEntry() {
        this.header = null;
    }

    ListenerEntry(Header header) {
        this.header = header;
    }

    public List<Reference<Object>> getReferences() {
        return getHeader().references;
    }

    public Header getHeader() {
        checkHasHeader();
        return header;
    }

    private void checkHasHeader() {
        if (header == null) {
            throw new IllegalStateException("attempt to obtain references from entry without header");
        }
    }

}
