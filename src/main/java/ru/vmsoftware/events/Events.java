package ru.vmsoftware.events;

import ru.vmsoftware.events.adapters.MethodAdapter;
import ru.vmsoftware.events.annotations.Listener;
import ru.vmsoftware.events.annotations.Listeners;
import ru.vmsoftware.events.filters.Filter;
import ru.vmsoftware.events.filters.Filters;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-27-04
 */
public class Events {

    /**
     * Shorthand for {@code Events.init(obj, Events.getManager())}
     *
     * @see #init(Object, Registrar)
     */
    public static void init(Object obj) {
        init(obj, manager);
    }

    /**
     * <p>Initializes object containing {@link ru.vmsoftware.events.annotations.Listener} and
     * {@link ru.vmsoftware.events.annotations.Listeners} annotations on methods by adding those listeners
     * to the current {@link EventManager} (which is returned by {@link #getManager()} method).</p>
     * <p>Note that if object contains {@link ru.vmsoftware.events.annotations.Listener listener} annotation with
     * specified {@link ru.vmsoftware.events.annotations.Listener#field field} - then
     * this field should already been initialized.</p>
     *
     * @param obj       object to initialize
     * @param registrar registrar for registering annotated {@link MethodAdapter method listeners}
     */
    public static Registrar init(Object obj, Registrar registrar) {
        Class<?> clazz = obj.getClass();
        do {
            for (Method m : clazz.getDeclaredMethods()) {
                final Listeners listeners = m.getAnnotation(Listeners.class);
                if (listeners != null) {
                    for (Listener l : listeners.value()) {
                        processAnnotation(registrar, obj, m, l);
                    }
                }

                final Listener listener = m.getAnnotation(Listener.class);
                if (listener != null) {
                    processAnnotation(registrar, obj, m, listener);
                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        return registrar;
    }

    /**
     * Shorthand for {@code Events.getManager().cleanup()}
     *
     * @see EventManager#cleanup()
     */
    public static void cleanup() {
        manager.cleanup();
    }

    /**
     * Shorthand for {@code Events.getManager().mute(obj)}
     *
     * @see EventManager#mute(Object)
     */
    public static void mute(Object obj) {
        manager.mute(obj);
    }

    /**
     * Shorthand for {@code Events.getManager().registrar()}
     *
     * @see EventManager#registrar()
     */
    public static Registrar registrar() {
        return manager.registrar();
    }

    /**
     * Shorthand for {@code Events.getManager().emitter(emitter)}
     *
     * @see EventManager#emitter(Object)
     */
    public static Emitter emitter(Object emitter) {
        return manager.emitter(emitter);
    }

    /**
     * Returns current {@link EventManager}
     *
     * @return current {@link EventManager}
     */
    public static EventManager getManager() {
        return manager;
    }

    /**
     * Shorthand for {@code Events.getManager().emit(emitter, event))}
     *
     * @see EventManager#emit(Object, Object)
     */
    public static boolean emit(Object emitter, Object type) {
        return getManager().emit(emitter, type);
    }

    /**
     * Shorthand for {@code Events.getManager().emit(emitter, type, data))}
     *
     * @see EventManager#emit(Object, Object, Object)
     */
    public static boolean emit(Object emitter, Object type, Object data) {
        return getManager().emit(emitter, type, data);
    }

    /**
     * Shorthand for {@code Events.getManager().listen(emitter, type, listener)}
     *
     * @see EventManager#listen(Object, Object, EventListener)
     */
    public static void listen(Object emitter, Object type, EventListener listener) {
        getManager().listen(emitter, type, listener);
    }

    static Object extractField(Object obj, String name) {
        final Class<?> clazz = obj.getClass();
        final Field field;
        try {
            field = clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("unable to find field '" + name +
                    "' in class '" + clazz + "'");
        }

        boolean wasAccessible = field.isAccessible();
        field.setAccessible(true);
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("unable to access field '" + name + "' in object of class '" +
                    clazz + "'");
        } finally {
            field.setAccessible(wasAccessible);
        }
    }

    static Filter<Object> getFilterByClassAndPattern(Class<?> clazz, String pattern) {
        final List<Filter<Object>> filters = new ArrayList<Filter<Object>>();
        if (clazz != Void.class) {
            filters.add(Filters.instanceOf(clazz));
        }
        if (pattern.length() > 0) {
            filters.add(Filters.toStringEqualTo(pattern));
        }
        return Filters.and(filters);
    }

    static void processAnnotation(Registrar registrar, Object obj, Method method, Listener listener) {
        Object emitter = null;
        if (listener.field().length() > 0) {
            emitter = extractField(obj, listener.field());
        }
        if (listener.tag().length() > 0) {
            Object container = emitter != null ? emitter : obj;
            if (!(container instanceof TagContainer<?>)) {
                throw new IllegalArgumentException("object should implement TagContainer interface");
            }
            emitter = ((TagContainer<?>) container).getByTag(listener.tag());
        }

        final Filter<Object> typeFilter = getFilterByClassAndPattern(listener.eventClass(), listener.event());
        final MethodAdapter methodAdapter = new MethodAdapter(obj, method);
        registrar.listen(emitter != null
                ? Filters.equalTo(emitter)
                : getFilterByClassAndPattern(listener.emitterClass(), listener.emitter()), typeFilter, methodAdapter);
    }

    static EventManager manager = new DefaultEventManager();
}
