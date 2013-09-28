package ru.vmsoftware.events.providers;

/**
 * <p>Provider is an interface which allows to generate or return early stored object</p>
 * <p>Note that clients which references implementations via this interface may change implementation dynamically</p>
 * <p>Primary this interface was developed to support single pointer to an object: </p>
 * <ul>
 *  <li>By strong reference ({@link StrongProvider}</li>
 *  <li>By any of {@link java.lang.ref.Reference} implementation
 *    ({@link ReferenceProvider}) essentially by {@link java.lang.ref.WeakReference}</li>
 * </ul>

 * @author Vyacheslav Mayorov
 * @since 2013-29-04
 */
public interface Provider<T> {

    /**
     * Generates or returns stored by this provider object
     * @return an provided object
     */
    T get();
}
