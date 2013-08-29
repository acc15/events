package ru.vmsoftware.events;

/**
 * <p>Emitter is an interface which implementations should simplify event
 * emitting by holding reference to real emitter object as internal state.</p>
 * <p>Also implementations may provide some additional functionality.</p><p><b>For example:</b>
 * emitter proxy can force event handling in another thread
 * (by calling {@link EventManager#emit(Object, Object, Object) eventManager.emit()} in another thread).</p>
 *
 * @author Vyacheslav Mayorov
 * @since 2013-29-05
 */
public interface Emitter {

    /**
     * Emits event of specified type
     *
     * @param type event type
     * @return result returned from {@link EventManager#emit(Object, Object)}
     * @see EventManager#emit(Object, Object)
     */
    boolean emit(Object type);

    /**
     * Emits event of specified type with attached data
     *
     * @param type event type
     * @param data event data
     * @return result returned from {@link EventManager#emit(Object, Object, Object)}
     * @see EventManager#emit(Object, Object, Object)
     */
    boolean emit(Object type, Object data);

}
