package ru.vmsoftware.events.annotations;

import ru.vmsoftware.events.references.ManagementType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-30-04
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ManagedBy {

    ManagementType value();

}
