package ru.vmsoftware.events;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-04-05
 */
public interface Equalizer<L, R> {
    boolean equals(L o1, R o2);
}
