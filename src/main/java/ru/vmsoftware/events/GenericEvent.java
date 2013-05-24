package ru.vmsoftware.events;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-24-05
 */
public class GenericEvent<E,T,D> {

    private E emitter;
    private T type;
    private D data;

    public GenericEvent(E emitter, T type, D data) {
        this.emitter = emitter;
        this.type = type;
        this.data = data;
    }

    public GenericEvent(E emitter, T type) {
        this.emitter = emitter;
        this.type = type;
    }

    public E getEmitter() {
        return emitter;
    }

    public T getType() {
        return type;
    }

    public D getData() {
        return data;
    }

}
