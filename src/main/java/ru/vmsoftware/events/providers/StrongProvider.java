package ru.vmsoftware.events.providers;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-04
 */
public class StrongProvider<T> implements Provider<T> {
    public StrongProvider(T ref) {
        this.ref = ref;
    }

    @Override
    public T get() {
        return ref;
    }

    private T ref;
}
