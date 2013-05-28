package ru.vmsoftware.events;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-05
 */
public interface Emitter {

    /**
     * Emits specific event
     * @param event event to emit
     * @return <p>result of calling {@link EventListener listeners} -
     *         if at least one returns <code>false</code>,
     *         then this method will also returns <code>false</code>
     *         and no more listeners will receive emitted event.</p>
     *         <p>Applications may use this result as they want.<br/>
     *         <i>For example: </i>listener may return <code>false</code>
     *         if emitted event {@code data} isn't valid</p>
     * @see EventListener#onEvent(Object)
     */
    boolean emit(Object event);

    /**
     * Convenient shorthand for {@link #emit(Object) emit(new GenericEvent(emitter, type))}
     * @see #emit(Object)
     * @see GenericEvent
     */
    boolean emit(Object emitter, Object type);

    /**
     * Convenient shorthand for {@link #emit(Object) emit(new GenericEvent(emitter, type, data))}
     * @see #emit(Object)
     * @see GenericEvent
     */
    boolean emit(Object emitter, Object type, Object data);
}
