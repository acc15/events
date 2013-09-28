package ru.vmsoftware.events.references;

import ru.vmsoftware.events.annotations.ManagedBy;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-05-05
 */
public class ManagementUtils {

    /**
     * Determines object {@link ManagementType} by using following rules: <ol>
     *  <li>If the class of given {@code obj} has annotation {@link ManagedBy} then
     *          use its {@link ru.vmsoftware.events.annotations.ManagedBy#value() value}</li>
     *  <li>If the class of given {@code obj} implements {@link ManagedObject} then
     *          use value returned by {@link ru.vmsoftware.events.references.ManagedObject#getManagementType() getManagementType()} method</li>
     *  <li>Otherwise use given {@code defaultType}</li>
     * </ol>
     * @param obj object for which need to determine {@link ManagementType}
     * @param defaultType default management type to use (see description above)
     * @return determined {@link ManagementType}
     */
    public static ManagementType getManagementType(Object obj, ManagementType defaultType) {
        final ManagedBy managedBy = obj.getClass().getAnnotation(ManagedBy.class);
        if (managedBy != null) {
            return managedBy.value();
        }
        if (obj instanceof ManagedObject) {
            return ((ManagedObject) obj).getManagementType();
        }
        return defaultType;
    }
}
