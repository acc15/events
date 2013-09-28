package ru.vmsoftware.events.references;

import ru.vmsoftware.events.providers.Provider;
import ru.vmsoftware.events.providers.Providers;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-09
 */
public class ReferenceHolder<T> implements ReferenceInitializer, Provider<T> {

    /**
     * Initializes holder with {@link ru.vmsoftware.events.providers.StrongProvider} pointing to given {@code value}
     * @param value value to initialize provider
     */
    public ReferenceHolder(T value) {
        this(Providers.strongRef(value));
    }

    /**
     * Initializes holder with given {@code provider}
     * @param provider initial provider
     */
    public ReferenceHolder(Provider<T> provider) {
        this.provider = provider;
    }

    public void initReferences(ReferenceManager referenceManager) {
        this.provider = referenceManager.manage(provider);
    }

    public T get() {
        return provider.get();
    }

    private Provider<T> provider;
}
