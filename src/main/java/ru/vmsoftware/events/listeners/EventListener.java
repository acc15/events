package ru.vmsoftware.events.listeners;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-12-04
 */
public interface EventListener<E, T, D> {

    /**
     * Listener callback method.
     * Called when emitter and event type are matched.
     * @param emitter event emitter
     * @param type    event type
     * @param data    event data or {@code null} if data wasn't specified when emitting
     * @return <code>true</code> if event handling should be continued,
     *         <code>false</code> if event handling should be stopped (i.e. next listener will not receive this event)
     */
    boolean handleEvent(E emitter, T type, D data);

    /**
     * <p>Checks whether specified {@code obj} is counterpart of this listener.</p>
     * <p>This check is used when {@link ru.vmsoftware.events.EventManager#mute(Object)} is called to
     * enable removal of listeners by supplying only they counterparts.</p>
     * <p>This is used, for example, to remove {@link ru.vmsoftware.events.listeners.adapters.MethodAdapter MethodAdapter}
     * by real listener object</p>
     * @param obj object to check
     * @return {@code true} if {@code obj} is counterpart of this listener.
     */
    boolean isCounterpart(Object obj);

}
