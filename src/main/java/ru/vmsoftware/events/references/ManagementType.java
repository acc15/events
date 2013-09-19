package ru.vmsoftware.events.references;

/**
 * Describes management type of listener.
 * If object is managed by container it will
 * become weakly reachable which enables automatic listener cleanup
 *
 * @author Vyacheslav Mayorov
 * @since 2013-30-04
 */
public enum ManagementType {

    /**
     * Object is managed by container. It will be weakly reachable
     */
    CONTAINER,

    /**
     * Object is managed manual.
     */
    MANUAL

}
