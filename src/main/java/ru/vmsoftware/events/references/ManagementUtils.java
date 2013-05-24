package ru.vmsoftware.events.references;

import ru.vmsoftware.events.annotations.ManagedBy;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-05-05
 */
public class ManagementUtils {
    public static ManagementType getManagementType(Object obj, ManagementType defaultType) {
        final ManagedBy managedBy = obj.getClass().getAnnotation(ManagedBy.class);
        return managedBy != null ? managedBy.value() : defaultType;
    }
}
