package ru.vmsoftware.events.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Wrapper to allow specifying multiple {@link Listener} annotations on single method.
 *
 * @author Vyacheslav Mayorov
 * @since 2013-27-04
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listeners {
    Listener[] value();
}
