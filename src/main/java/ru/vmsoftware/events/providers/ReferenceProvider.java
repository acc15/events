package ru.vmsoftware.events.providers;

import java.lang.ref.Reference;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-04
 */
public class ReferenceProvider<T> implements Provider<T> {

    public ReferenceProvider(Reference<T> ref) {
        this.ref = ref;
    }

    @Override
    public T get() {
        return ref.get();
    }

    private Reference<T> ref;
}
