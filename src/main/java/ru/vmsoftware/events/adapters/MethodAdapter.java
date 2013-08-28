package ru.vmsoftware.events.adapters;

import org.apache.commons.lang.ObjectUtils;
import ru.vmsoftware.events.EventListener;
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
public class MethodAdapter implements EventListener, ContainerManaged {

    public MethodAdapter(Object obj, String methodName) {
        this(obj, findListenerMethod(obj, methodName));
    }

    public MethodAdapter(Object obj, Method method) {
        this.provider = new StrongProvider<Object>(obj);
        this.method = method;
    }

    public boolean onEvent(Object emitter, Object type, Object data) {
        final Object object = provider.get();
        if (object == null) {
            return true;
        }

        final int paramCount = method.getParameterTypes().length;

        final boolean wasAccessible = method.isAccessible();
        method.setAccessible(true);
        try {
            // This duplication is an optimization.
            // Instead of creating list and then making subList(size - parameterCount, size)
            // this will make simple and quick method invocation.

            final Object result;
            switch (paramCount) {
                case 0:
                    result = method.invoke(object);
                    break;

                case 1:
                    result = method.invoke(object, data);
                    break;

                case 2:
                    result = method.invoke(object, type, data);
                    break;

                case 3:
                    result = method.invoke(object, emitter, type, data);
                    break;

                default:
                    throw new IllegalArgumentException("listener method has too many arguments: " + method);
            }
            if (result instanceof Boolean) {
                return (Boolean) result;
            }
            return true;

        } catch (IllegalAccessException e) {
            throw new RuntimeException("unable to access listener method: " + method, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("unable to invoke listener method: " + method, e);
        } finally {
            method.setAccessible(wasAccessible);
        }
    }

    public boolean isCounterpart(Object obj) {
        return ObjectUtils.equals(provider.get(), obj);
    }

    public void initReferences(ReferenceContainer referenceContainer) {
        provider = referenceContainer.manage(provider);
    }

    private static Method findListenerMethod(Object obj, String methodName) {

        final Class<?> type = obj.getClass();
        final Method[] methods = type.getDeclaredMethods();

        Method foundMethod = null;
        for (Method m : methods) {
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

    private Provider<?> provider;
    private Method method;

}
