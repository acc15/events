package ru.vmsoftware.events.references;

import java.util.List;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-25-05
 */
public interface CompositeObject<T> {
    List<T> getUnderlyingObjects();
}
