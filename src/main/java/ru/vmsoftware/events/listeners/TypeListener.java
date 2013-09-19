package ru.vmsoftware.events.listeners;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
public interface TypeListener<T,D> {

    /**
     * Listener callback method
     * @param type    event type
     * @param data    event data or {@code null} if data wasn't specified when emitting
     * @see EventListener
     */
    void onEvent(T type, D data);

}
