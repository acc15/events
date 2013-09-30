package ru.vmsoftware.events.collections.entry;

import ru.vmsoftware.events.references.ReferenceInitializer;

import java.lang.ref.Reference;
import java.util.List;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-29-09
 */
public interface WeakContainer extends ReferenceInitializer {
    void setReferences(List<Reference<?>> refs);
    List<Reference<?>> getReferences();
}
