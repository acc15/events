package ru.vmsoftware.events.references;

import ru.vmsoftware.events.providers.Provider;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-05-05
 */
public interface ReferenceManager {

    /**
     * Manages given provider returning either {@link ru.vmsoftware.events.providers.ReferenceProvider ReferenceProvider} with
     * {@link java.lang.ref.WeakReference WeakReference} pointing to object from given {@code provider} or given {@code provider}.
     * Which type of provider to return should be determined by evaluated {@link ManagementType}. Rules for
     * determining of provider object {@link ManagementType} are described in
     * {@link ManagementUtils#getManagementType(Object, ManagementType)} method.
     * @param provider provider to manage
     * @param defaultType default management type
     * @param <T> type of provider object
     * @return managed provider
     * @see ManagementUtils#getManagementType(Object, ManagementType)
     * @see ManagementType
     */
    <T> Provider<T> manage(Provider<T> provider, ManagementType defaultType);

    /**
     * Shorthand for {@link #manage(ru.vmsoftware.events.providers.Provider, ManagementType)
     *      manage(Provider, ManagementType.CONTAINER)}
     * @param provider initial provider
     * @param <T> type of object the provider returns
     * @return  provider
     */
    <T> Provider<T> manage(Provider<T> provider);
}
