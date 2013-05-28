package ru.vmsoftware.events;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-12-04
 */
public interface EventManager extends Registrar, Emitter {

    /**
     * Creates registrar which simplifies listener management by providing
     * {@link Registrar#cleanup() cleanup()} method
     * @return fresh {@link Registrar}
     */
    Registrar createRegistrar();


}
