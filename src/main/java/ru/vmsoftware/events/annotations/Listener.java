package ru.vmsoftware.events.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-27-04
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {

    /**
     * Field name of emitter object instance.
     * Allows to listen specific instance of emitter which is stored in specified field of listener object
     *
     * @return field name where emitter instance should be looked up
     */
    String field() default "";

    /**
     * Tag of specific emitter instance. Emitter will be obtained via {@link ru.vmsoftware.events.TagContainer}
     * interface. <ul>
     * <li>If {@link #field()} is specified then object in specified field should implement
     * {@link ru.vmsoftware.events.TagContainer TagContainer} interface</li>
     * <li>If {@link #field()} is empty then current object (which has method with this annotation) should
     * implement {@link ru.vmsoftware.events.TagContainer TagContainer} interface</li>
     * </ul>
     *
     * @return emitter instance tag
     */
    String tag() default "";

    /**
     * Emitter class allows to listen events from emitters which are instances of specified class
     *
     * @return emitter class
     */
    Class<?> emitterClass() default Void.class;

    /**
     * Emitter value allows to listen events from emitters which matches specified {@link #toString()} value
     *
     * @return emitter {@link #toString()} value
     */
    String emitter() default "";

    /**
     * Event class allows to listen events which are instances of specified class
     *
     * @return event class
     */
    Class<?> eventClass() default Void.class;

    /**
     * Event value allows to listen events which has specified {@link #toString()} value
     *
     * @return event value
     */
    String event() default "";
}
