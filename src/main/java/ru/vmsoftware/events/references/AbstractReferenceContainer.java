package ru.vmsoftware.events.references;

import ru.vmsoftware.events.providers.Provider;

import static ru.vmsoftware.events.references.ManagementType.CONTAINER;
import static ru.vmsoftware.events.references.ManagementUtils.getManagementType;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-05-05
 */
public abstract class AbstractReferenceContainer implements ReferenceContainer {

    @Override
    public <T> Provider<T> manage(Provider<T> provider, ManagementType defaultType) {
        final T obj = provider.get();
        if (obj == null) {
            return provider;
        }
        if (getManagementType(obj, defaultType) != CONTAINER) {
            if (obj instanceof ContainerManaged) {
                ((ContainerManaged) obj).initReferences(this);
            }
            return provider;
        }
        return manageObject(obj);
    }

    @Override
    public <T> Provider<T> manage(Provider<T> provider) {
        return manage(provider, CONTAINER);
    }

    protected abstract <T> Provider<T> manageObject(T obj);
}
