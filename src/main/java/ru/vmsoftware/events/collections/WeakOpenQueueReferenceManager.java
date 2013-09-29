package ru.vmsoftware.events.collections;

import ru.vmsoftware.events.collections.entry.WeakContainer;
import ru.vmsoftware.events.providers.Provider;
import ru.vmsoftware.events.providers.Providers;
import ru.vmsoftware.events.references.AbstractReferenceManager;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-09
 */
public class WeakOpenQueueReferenceManager<E extends WeakContainer<Object>> extends AbstractReferenceManager {

    private final E entry;
    private final WeakOpenQueue<Object,E> weakOpenQueue;
    private final List<Reference<Object>> references = new ArrayList<Reference<Object>>();

    public WeakOpenQueueReferenceManager(WeakOpenQueue<Object,E> weakOpenQueue, E entry) {
        this.weakOpenQueue = weakOpenQueue;
        this.entry = entry;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> Provider<T> manageObject(T obj) {
        final Reference<Object> ref = weakOpenQueue.createReference(entry, obj);
        references.add(ref);
        return (Provider<T>)Providers.ref(ref);
    }

    public List<Reference<Object>> getReferences() {
        return Collections.unmodifiableList(references);
    }
}
