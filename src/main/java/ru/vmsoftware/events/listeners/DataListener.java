package ru.vmsoftware.events.listeners;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
public interface DataListener<D> {

    /**
     * Listener callback method
     * @param data attached event data or <code>null</code>
     * @see EventListener
     */
    void onEvent(D data);

}
