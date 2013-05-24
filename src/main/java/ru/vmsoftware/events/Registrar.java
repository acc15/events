package ru.vmsoftware.events;

/**
* @author Vyacheslav Mayorov
* @since 2013-30-04
*/
public interface Registrar {

    /**
     * Registers listener.
     * @param emitter event emitter
     * @param event event type
     * @param listener listener
     */
    void listen(Object emitter, Object event, EventListener<?, ?, ?> listener);

    /**
     * Removes specified listener from registrar
     * @param listener listener to remove
     */
    void mute(Object listener);

    /**
     * Removes all listeners registered by this registrar
     */
    void cleanup();

    /**
     * Checks whether this registrar doesn't register any listener
     * @return {@code true} if this registrar doesn't register any listener, {@code false} otherwise
     */
    boolean isClean();

}
