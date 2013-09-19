package ru.vmsoftware.events.references;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.vmsoftware.events.providers.Provider;
import ru.vmsoftware.events.providers.Providers;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-05-05
 */
public class AbstractReferenceContainerTest {

    @Mock
    private AbstractReferenceContainer container;

    @Mock
    private ContainerManaged containerManaged;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(container.manage(Matchers.<Provider<?>>any())).thenCallRealMethod();
        when(container.manage(Matchers.<Provider<?>>any(), Matchers.<ManagementType>any())).thenCallRealMethod();
    }

    @Test
    public void testManageReturnsGivenProviderIfObjectIsNull() throws Exception {
        final Provider<Object> nullProvider = new Provider<Object>() {
            public Object get() {
                return null;
            }
        };
        assertThat(container.manage(nullProvider)).isSameAs(nullProvider);
    }

    @Test
    public void testManageReturnsGivenProviderIfObjectIsManualManagedByAnnotation() throws Exception {
        final Provider<Object> mmProvider = Providers.<Object>strongRef(new ManagementUtilsTest.MMObject());
        assertThat(container.manage(mmProvider)).isSameAs(mmProvider);
    }

    @Test
    public void testManageCallsManageObjectIfObjectIsContainerManagedByAnnotation() throws Exception {
        final ManagementUtilsTest.CMObject cmObject = new ManagementUtilsTest.CMObject();
        final Provider<ManagementUtilsTest.CMObject> expectedProvider = Providers.strongRef(cmObject);
        final Provider<ManagementUtilsTest.CMObject> givenProvider = Providers.strongRef(cmObject);

        when(container.manageObject(cmObject)).thenReturn(expectedProvider);

        assertThat(container.manage(givenProvider)).isSameAs(expectedProvider);
        verify(container).manageObject(cmObject);
    }

    @Test
    public void testManageUsesContainerManagementTypeByDefault() throws Exception {

        final ManagementUtilsTest.DMObject dmObject = new ManagementUtilsTest.DMObject();
        final Provider<ManagementUtilsTest.DMObject> expectedProvider = Providers.strongRef(dmObject);
        final Provider<ManagementUtilsTest.DMObject> givenProvider = Providers.strongRef(dmObject);

        when(container.manageObject(dmObject)).thenReturn(expectedProvider);
        assertThat(container.manage(givenProvider)).isSameAs(expectedProvider);
        verify(container).manageObject(dmObject);

    }

    @Test
    public void testManageUsesGivenDefaultManagementType() throws Exception {
        final ManagementUtilsTest.DMObject dmObject = new ManagementUtilsTest.DMObject();
        final Provider<ManagementUtilsTest.DMObject> expectedProvider = Providers.strongRef(dmObject);
        assertThat(container.manage(expectedProvider, ManagementType.MANUAL)).isSameAs(expectedProvider);
    }

    @Test
    public void testManagerShouldntCallContainerManagedWhenManagementTypeIsContainer() throws Exception {
        container.manage(Providers.strongRef(containerManaged), ManagementType.CONTAINER);
        verifyZeroInteractions(containerManaged);
    }

    @Test
    public void testManageShouldCallManageObjectWhenManagementTypeIsManualAndObjectIsContainerManaged()
            throws Exception {
        container.manage(Providers.strongRef(containerManaged), ManagementType.MANUAL);
        verify(containerManaged).initReferences(container);
    }
}
