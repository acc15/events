package ru.vmsoftware.events.references;

import ru.vmsoftware.events.providers.Provider;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-05-05
 */
public interface ReferenceContainer {
    <T> Provider<T> manage(Provider<T> provider, ManagementType defaultType);

    <T> Provider<T> manage(Provider<T> provider);
}
