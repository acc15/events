package ru.vmsoftware.events.providers;

import java.lang.ref.Reference;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-04
 */
class ReferenceProvider<T> implements Provider<T> {

    ReferenceProvider(Reference<T> ref) {
        this.ref = ref;
    }

    public T get() {
        return ref.get();
    }

    private final Reference<T> ref;
}
