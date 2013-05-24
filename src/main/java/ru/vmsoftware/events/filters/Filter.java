package ru.vmsoftware.events.filters;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-18-04
 */
public interface Filter<T> {

    /**
     * Checks that specified value satisfies current filter.
     * @param value value to check in filter
     * @return <p>{@code true} if specified {@code value} is satisfies filter.
     *            This {@code value} shouldn't be filtered.</p>
     *         <p>{@code false} if {@code value} should be filtered</p>
     */
    boolean filter(T value);
}
