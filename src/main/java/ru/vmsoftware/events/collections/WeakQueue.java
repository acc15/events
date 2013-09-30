package ru.vmsoftware.events.collections;

import org.apache.commons.lang.ObjectUtils;
import ru.vmsoftware.events.collections.entry.AbstractEntryFactory;
import ru.vmsoftware.events.collections.entry.SimpleConcurrentEntry;
import ru.vmsoftware.events.collections.entry.WeakContainer;
import ru.vmsoftware.events.providers.Provider;
import ru.vmsoftware.events.providers.Providers;
import ru.vmsoftware.events.references.ManagementType;
import ru.vmsoftware.events.references.ReferenceManager;

import java.lang.ref.Reference;
import java.util.List;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-09
 */
public class WeakQueue<T> implements SimpleQueue<T> {

    private static class WeakEntry<T> extends SimpleConcurrentEntry<WeakEntry<T>> implements WeakContainer {

        private List<Reference<?>> refs = null;
        private Provider<T> valueProvider;

        private WeakEntry() {
        }

        private WeakEntry(T valueProvider) {
            this.valueProvider = Providers.strongRef(valueProvider);
        }

        @SuppressWarnings("unchecked")
        public T getValue() {
            if (valueProvider == null) {
                throw new IllegalStateException("attempt to obtain value from service entry");
            }
            return valueProvider.get();
        }

        public void initReferences(ReferenceManager referenceManager) {
            valueProvider = referenceManager.manage(valueProvider, ManagementType.CONTAINER);
        }

        public void setReferences(List<Reference<?>> refs) {
            this.refs = refs;
        }

        public List<Reference<?>> getReferences() {
            return refs;
        }
    }

    private static class WeakEntryFactory<T> extends AbstractEntryFactory<WeakEntry<T>> {
        private static final WeakEntryFactory instance = new WeakEntryFactory();

        @SuppressWarnings("unchecked")
        public static <T> WeakEntryFactory<T> getInstance() {
            return (WeakEntryFactory<T>)instance;
        }

        private WeakEntryFactory() {
        }

        @Override
        protected WeakEntry<T> createEntry(boolean marker) {
            return new WeakEntry<T>();
        }
    }

    private WeakOpenQueue<WeakEntry<T>> queue = new WeakOpenQueue<WeakEntry<T>>(
            new ConcurrentOpenLinkedQueue<WeakEntry<T>>(WeakEntryFactory.<T>getInstance()));

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void clear() {
        queue.clear();
    }

    public void add(T value) {
        queue.add(new WeakEntry<T>(value));
    }

    public boolean remove(T value) {
        WeakEntry<T> e;
        final SimpleIterator<WeakEntry<T>> iter = queue.iterator();
        while ((e = iter.next()) != null) {
            if (ObjectUtils.equals(e.getValue(), value)) {
                return true;
            }
        }
        return false;
    }

    public SimpleIterator<T> iterator() {
        return new SimpleIterator<T>() {

            private SimpleIterator<WeakEntry<T>> iter = queue.iterator();

            public T next() {
                final WeakEntry<T> e = iter.next();
                if (e == null) {
                    return null;
                }
                return e.getValue();
            }

            public boolean remove() {
                return iter.remove();
            }
        };
    }

    @Override
    public String toString() {
        return SimpleQueueUtils.toString(this);
    }
}
