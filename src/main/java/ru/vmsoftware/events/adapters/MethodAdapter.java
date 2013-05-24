package ru.vmsoftware.events.adapters;

import org.apache.commons.lang.ObjectUtils;
import ru.vmsoftware.events.EventListener;
import ru.vmsoftware.events.GenericEvent;
import ru.vmsoftware.events.providers.Provider;
import ru.vmsoftware.events.providers.StrongProvider;
import ru.vmsoftware.events.references.ContainerManaged;
import ru.vmsoftware.events.references.ReferenceContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-27-04
 */
public class MethodAdapter implements EventListener<Object>, ContainerManaged {

    public MethodAdapter(Object obj, String methodName) {
        this(obj, findListenerMethod(obj, methodName));
    }

    public MethodAdapter(Object obj, Method method) {
        this.provider = new StrongProvider<Object>(obj);
        this.method = method;
    }

    @Override
    public boolean onEvent(Object event) {
        if (method == null) {
            return true;
        }

        final Object object = provider.get();
        if (object == null) {
            return true;
        }

        final Class<?>[] parameterTypes = method.getParameterTypes();
        final int parameterCount = parameterTypes.length;

        Object result = null;

        boolean wasAccessible = method.isAccessible();
        method.setAccessible(true);
        try {

            if (parameterCount == 0) {
                result = method.invoke(object);
            } else if (event instanceof GenericEvent<?,?,?>) {
                final GenericEvent<?,?,?> genericEvent = (GenericEvent<?,?,?>) event;
                switch (parameterCount) {
                    case 1:
                        result = method.invoke(object, parameterTypes[0].isAssignableFrom(GenericEvent.class)
                            ? genericEvent
                            : genericEvent.getData());
                        break;

                    case 2:
                        result = method.invoke(object, genericEvent.getType(), genericEvent.getData());
                        break;

                    case 3:
                        result = method.invoke(object,
                            genericEvent.getEmitter(), genericEvent.getType(), genericEvent.getData());
                        break;

                    default:
                        throw tooManyArguments();
                }
            } else {
                if (parameterCount > 1) {
                    throw tooManyArguments();
                }
                result = method.invoke(object, event);
            }

        } catch (IllegalAccessException e) {
            throw new RuntimeException("unable to access listener method: " + method, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("unable to invoke listener method: " + method, e);
        } finally {
            method.setAccessible(wasAccessible);
        }
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        return true;
    }

    @Override
    public boolean isCounterpart(Object obj) {
        return ObjectUtils.equals(provider.get(), obj);
    }

    @Override
    public void initReferences(ReferenceContainer referenceContainer) {
        provider = referenceContainer.manage(provider);
    }

    private static Method findListenerMethod(Object obj, String methodName) {

        final Class<?> type = obj.getClass();
        final Method[] methods = type.getDeclaredMethods();

        Method foundMethod = null;
        for (Method m: methods) {
            if (!methodName.equals(m.getName())) {
                continue;
            }

            final Class<?>[] paramTypes = m.getParameterTypes();
            if (foundMethod == null ||
                (paramTypes.length < 4 && paramTypes.length > foundMethod.getParameterTypes().length)) {
                foundMethod = m;
            }
        }
        if (foundMethod == null) {
            throw new IllegalArgumentException("unable to find method with name '" +
                    methodName + "' in class '" + type.toString() + "'");
        }
        return foundMethod;

    }

    private IllegalArgumentException tooManyArguments() {
        return new IllegalArgumentException("listener method has too many arguments: " + method);
    }

    private Provider<?> provider;
    private Method method;

}
