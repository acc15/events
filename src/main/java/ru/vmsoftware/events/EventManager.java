package ru.vmsoftware.events;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-12-04
 */
public interface EventManager extends Registrar {

    /**
     * Creates registrar which simplifies listener management by providing
     * {@link Registrar#cleanup() cleanup()} method
     * @return fresh {@link Registrar}
     */
    Registrar createRegistrar();

    /**
     * Emits specific event from {@code emitter}
     * @param event event to emit
     * @return <p>result of calling {@link EventListener listeners} -
     *         if at least one returns <code>false</code>,
     *         then this method will also returns <code>false</code>
     *         and no more listeners will receive emitted event.</p>
     *         <p>Applications may use this result as they want.<br/>
     *         <b>For example: </b>listener may return <code>false</code> if emitted {@code data} isn't valid
     * @see EventListener#onEvent(Object)
     */
    boolean emit(Object event);

}
