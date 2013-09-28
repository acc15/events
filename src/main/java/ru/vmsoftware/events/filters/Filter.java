package ru.vmsoftware.events.filters;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-18-04
 */
public interface Filter<T> {

    /**
     * Checks whether given {@code value} satisfies current filter or not
     * @param value value to check
     * @return <p>{@code true} if given {@code value} satisfies filter</p>
     *         <p>{@code false} if {@code value} doesn't satisfy filter</p>
     */
    boolean filter(T value);

}
