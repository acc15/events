package ru.vmsoftware.events.providers;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-04
 */
class StrongProvider<T> implements Provider<T> {
    StrongProvider(T ref) {
        this.ref = ref;
    }

    public T get() {
        return ref;
    }

    private final T ref;
}
