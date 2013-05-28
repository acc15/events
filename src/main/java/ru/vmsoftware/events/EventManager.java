package ru.vmsoftware.events;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-12-04
 */
public interface EventManager extends Registrar {

    /**
     * Creates new listener registrar
     * @return {@link Registrar}
     * @see Registrar
     */
    Registrar createRegistrar();

    /**
     * Creates new event emitter
     * @param emitter real event emitter
     * @return {@link Emitter}
     * @see Emitter
     *
     * TODO write tests & implement
     */
    //Emitter createEmitter(Object emitter);

    /**
     * Convenient shorthand for {@link #emit(Object,Object,Object) emit(emitter, type, null))}
     * @see #emit(Object,Object,Object)
     */
    boolean emit(Object emitter, Object type);

    /**
     * Emits event
     * @param emitter real event emitter
     * @param type event type
     * @param data event data
     * @return <p>result of calling {@link EventListener listeners} -
     *         if at least one returns <code>false</code>,
     *         then this method will also returns <code>false</code>
     *         and no more listeners will receive emitted event.</p>
     *         <p>Applications may use this result as they want.<br/>
     *         <i>For example: </i>listener may return <code>false</code>
     *         if emitted event {@code data} isn't valid</p>
     * @see EventListener#onEvent(Object, Object, Object)
     */
    boolean emit(Object emitter, Object type, Object data);

}
