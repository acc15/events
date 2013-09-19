package ru.vmsoftware.events.filters;

import ru.vmsoftware.events.providers.Provider;
import ru.vmsoftware.events.providers.Providers;
import ru.vmsoftware.events.references.ReferenceInitializer;
import ru.vmsoftware.events.references.ReferenceManager;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-03-05
 */
class SameInstanceFilter<T> implements Filter<T>, ReferenceInitializer {
    SameInstanceFilter(T instance) {
        this.instance = Providers.strongRef(instance);
    }

    public boolean filter(T value) {
        return instance.get() == value;
    }

    public void initReferences(ReferenceManager referenceManager) {
        instance = referenceManager.manage(instance);
    }

    private Provider<T> instance;

}
