package ru.vmsoftware.events.annotations;

import java.lang.annotation.*;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-27-04
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {

    /**
     * Field name of emitter object instance.
     * If you want to listen specific instance of emitter then you can specify field name where
     * emitter instance can be found.
     * @return field name where emitter instance should be looked up
     */
    String field() default "";

    /**
     * Tag of specific emitter instance. Emitter will be obtained via {@link ru.vmsoftware.events.TagContainer}
     * interface. <ul>
     *     <li>If {@link #field()} is specified then object in specified field should implement
     * {@link ru.vmsoftware.events.TagContainer TagContainer} interface</li>
     *  <li>If {@link #field()} is empty then current object (which has method with this annotation) should
     *      implement {@link ru.vmsoftware.events.TagContainer TagContainer} interface</li>
     * </ul>
     * @return emitter instance tag
     */
    String tag() default "";

    /**
     * Emitter class allows to listen only events which are emitter by instances of specified class
     * @return emitter class
     */
    Class<?> emitterType() default Void.class;

    /**
     * Emitter value allows to listen only events from emitter which has specified {@link #toString()} value
     * @return emitter pattern
     */
    String emitter() default "";

    /**
     * Event class allows to listen only events which are instances of specified class
     * @return event class
     */
    Class<?> eventType() default Void.class;

    /**
     * Event value allows to listen only events which has specified {@link #toString()} value
     * @return event value
     */
    String event() default "";
}
