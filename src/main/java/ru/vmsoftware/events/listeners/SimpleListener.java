package ru.vmsoftware.events.listeners;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
public interface SimpleListener<E,T,D> {

    /**
     * Listener callback method
     * @param emitter event emitter
     * @param type    event type
     * @param data    event data or {@code null} if data wasn't specified when emitting
     * @see EventListener
     */
    void onEvent(E emitter, T type, D data);

}
