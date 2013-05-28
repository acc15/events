package ru.vmsoftware.events;

/**
 * <p>Registrar is very suitable in the cases when listeners should be removed manually from {@link EventManager}
 * before real listener object will be garbage collected (in this case listeners will be removed automatically)</p>
 * <p>Registrar provides methods by using which clients can {@link #listen(Object, Object, EventListener) register},
 * {@link #mute(Object) unregister} or {@link #cleanup() remove all} registered by this registrar listeners</p>
* @author Vyacheslav Mayorov
* @since 2013-30-04
*/
public interface Registrar {

    /**
     * Registers listener
     * @param emitter emitter
     * @param type type
     * @param listener listener
     */
    void listen(Object emitter, Object type, EventListener<?,?,?> listener);

    /**
     * Removes specified listener registered by {@code this} registrar
     * @param listener listener to remove
     */
    void mute(Object listener);

    /**
     * Removes all listeners registered by {@code this} registrar
     */
    void cleanup();

    /**
     * Checks whether this registrar doesn't register any listener
     * @return {@code true} if this registrar doesn't register any listener, {@code false} otherwise
     */
    boolean isClean();

}
