package ru.vmsoftware.events.annotations;

import ru.vmsoftware.events.references.ManagementType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking classes as container or manual managed
 * @author Vyacheslav Mayorov
 * @since 2013-30-04
 * @see ManagementType
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ManagedBy {

    /**
     * Management type to use for object of annotated class
     * @return management type to use
     */
    ManagementType value();

}
