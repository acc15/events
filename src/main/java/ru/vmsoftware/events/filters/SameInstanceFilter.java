package ru.vmsoftware.events.filters;

import ru.vmsoftware.events.references.ReferenceContainer;
import ru.vmsoftware.events.references.ContainerManaged;
import ru.vmsoftware.events.providers.Provider;
import ru.vmsoftware.events.providers.StrongProvider;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-03-05
 */
public class SameInstanceFilter<T> implements Filter<T>, ContainerManaged {
    public SameInstanceFilter(T instance) {
        this.instance = new StrongProvider<T>(instance);
    }

    @Override
    public boolean filter(T value) {
        return instance.get() == value;
    }

    @Override
    public void initReferences(ReferenceContainer referenceContainer) {
        instance = referenceContainer.manage(instance);
    }

    private Provider<T> instance;

}
