package ru.vmsoftware.events.references;

import org.junit.Test;
import ru.vmsoftware.events.annotations.ManagedBy;

import static org.fest.assertions.api.Assertions.assertThat;
import static ru.vmsoftware.events.references.ManagementType.*;
import static ru.vmsoftware.events.references.ManagementUtils.getManagementType;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-05-05
 */
public class ManagementUtilsTest {

    @ManagedBy(CONTAINER)
    static class CMObject {
    }

    @ManagedBy(MANUAL)
    static class MMObject {
    }

    static class DMObject {
    }

    @Test
    public void testGetManagementType() throws Exception {
        final CMObject cmObject = new CMObject();
        final MMObject mmObject = new MMObject();
        final DMObject dmObject = new DMObject();
        assertThat(getManagementType(cmObject, MANUAL)).isEqualTo(CONTAINER);
        assertThat(getManagementType(cmObject, CONTAINER)).isEqualTo(CONTAINER);
        assertThat(getManagementType(mmObject, MANUAL)).isEqualTo(MANUAL);
        assertThat(getManagementType(mmObject, CONTAINER)).isEqualTo(MANUAL);
        assertThat(getManagementType(dmObject, MANUAL)).isEqualTo(MANUAL);
        assertThat(getManagementType(dmObject, CONTAINER)).isEqualTo(CONTAINER);
    }
}
