package ru.vmsoftware.events.filters;

import ru.vmsoftware.events.providers.Provider;
import ru.vmsoftware.events.providers.StrongProvider;
import ru.vmsoftware.events.references.ContainerManaged;
import ru.vmsoftware.events.references.ReferenceContainer;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-03-05
 */
class SameInstanceFilter<T> extends AbstractSimpleFilter<T> implements ContainerManaged {
    SameInstanceFilter(T instance) {
        this.instance = new StrongProvider<T>(instance);
    }

    public boolean filter(T value) {
        return instance.get() == value;
    }

    public void initReferences(ReferenceContainer referenceContainer) {
        instance = referenceContainer.manage(instance);
    }

    private Provider<T> instance;

}
