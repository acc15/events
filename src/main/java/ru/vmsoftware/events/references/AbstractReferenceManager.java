package ru.vmsoftware.events.references;

import ru.vmsoftware.events.providers.Provider;

import java.util.Collections;
import java.util.List;

import static ru.vmsoftware.events.references.ManagementType.CONTAINER;
import static ru.vmsoftware.events.references.ManagementUtils.getManagementType;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-05-05
 */
public abstract class AbstractReferenceManager implements ReferenceManager {

    public <T> Provider<T> manage(Provider<T> provider, ManagementType defaultType) {
        final T obj = provider.get();
        if (obj == null) {
            return provider;
        }
        if (getManagementType(obj, defaultType) != CONTAINER) {
            initObjects(Collections.singletonList(obj));
            return provider;
        }
        return manageObject(obj);
    }

    public <T> Provider<T> manage(Provider<T> provider) {
        return manage(provider, CONTAINER);
    }

    protected abstract <T> Provider<T> manageObject(T obj);

    private void initObjects(List<?> objects) {
        for (Object obj : objects) {
            if (obj instanceof ReferenceInitializer) {
                ((ReferenceInitializer) obj).initReferences(this);
            } else if (obj instanceof CompositeObject<?>) {
                initObjects(((CompositeObject<?>) obj).getUnderlyingObjects());
            }
        }
    }
}
