package ru.vmsoftware.events.collections.entry;

import java.lang.ref.Reference;
import java.util.List;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-09
 */
public interface WeakContainer<T> {
    List<Reference<T>> getReferences();
}
