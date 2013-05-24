package ru.vmsoftware.events;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.vmsoftware.events.adapters.MethodAdapter;
import ru.vmsoftware.events.adapters.SimpleAdapter;
import ru.vmsoftware.events.annotations.ManagedBy;
import ru.vmsoftware.events.filters.AnyFilter;
import ru.vmsoftware.events.filters.EqualsFilter;
import ru.vmsoftware.events.references.ManagementType;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
public class DefaultEventManagerTest {

    @Mock
    private EventListener<Object,Object,Object> listener;

    @Mock
    private EventListener<Object,Object,Object> listener2;

    private Object eventType = new Object();
    private Object data = new Object();

    private DefaultEventManager manager = new DefaultEventManager();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(listener.onEvent(this, eventType, null)).thenReturn(true);
        Mockito.when(listener2.onEvent(this, eventType, null)).thenReturn(true);
    }

    @Test(expected = NullPointerException.class)
    public void testListenDoesntAcceptNullPointers() throws Exception {
        manager.listen(null, AnyFilter.getInstance(), listener);
    }

    @Test(expected = NullPointerException.class)
    public void testListenDoesntAcceptNullPointers2() throws Exception {
        manager.listen(this, null, listener);
    }

    @Test(expected = NullPointerException.class)
    public void testListenDoesntAcceptNullPointers3() throws Exception {
        manager.listen(this, AnyFilter.getInstance(), null);
    }

    @Test(expected = NullPointerException.class)
    public void testEmitDoesntAcceptNullPointers() throws Exception {
        manager.emit(null, eventType, data);
    }

    @Test(expected = NullPointerException.class)
    public void testEmitDoesntAcceptNullPointers2() throws Exception {
        manager.emit(this, null, data);
    }

    @Test
    public void testEmitAcceptNullPointersForData() throws Exception {
        manager.emit(this, eventType, null);
    }

    @Test
    public void testEmittedEventsDeliveredCorrectly() throws Exception {
        manager.listen(this, AnyFilter.getInstance(), listener);
        manager.emit(this, eventType, data);
        Mockito.verify(listener).onEvent(this, eventType, data);
    }

    @Test
    public void testEmitShouldCallListenersRegisteredWithInterfaceOrParentClass() throws Exception {
        manager.listen(CharSequence.class, AnyFilter.getInstance(), listener);
        final String emitter = "abc";
        manager.emit(emitter, eventType, data);
        Mockito.verify(listener).onEvent(emitter, eventType, data);
    }

    @Test
    public void testEmittedEventsDeliveredIfListenerAddForEmitterClass() throws Exception {
        manager.listen(DefaultEventManagerTest.class, AnyFilter.getInstance(), listener);
        manager.emit(this, eventType, data);
        Mockito.verify(listener).onEvent(this, eventType, data);
    }

    @Test
    public void testManagerDoesntCallListenerIfEventTypeNotMatched() throws Exception {
        manager.listen(this, new EqualsFilter<Object>(eventType), listener);
        manager.listen(this, new EqualsFilter<String>("xyz"), listener2);
        verifyOnlyFirstCalled();
    }

    @Test
    public void testManagerDoesntCallListenerIfEmitterNotMatched() throws Exception {
        manager.listen(this, AnyFilter.getInstance(), listener);
        manager.listen(AnyFilter.class, AnyFilter.getInstance(), listener2);
        verifyOnlyFirstCalled();
    }

    @Test
    public void testManagerBreaksExecutionIfSomeoneListenerReturnsFalse() throws Exception {
        Mockito.when(listener.onEvent(this, eventType, data)).thenReturn(false);

        manager.listen(this, AnyFilter.getInstance(), listener);
        manager.listen(this, AnyFilter.getInstance(), listener2);
        manager.emit(this, eventType, data);

        Mockito.verify(listener).onEvent(this, eventType, data);
        Mockito.verify(listener2, Mockito.never()).onEvent(Matchers.any(), Matchers.any(), Matchers.any());
    }

    @ManagedBy(ManagementType.CONTAINER)
    private static class ManagedContainerListener extends SimpleAdapter {
        @Override
        public boolean onEvent(Object emitter, Object type, Object data) {
            return true;
        }
    }

    @Test
    public void testManagerCleanupsDataIfListenerIsManagedByContainer() throws Exception {
        ManagedContainerListener l = new ManagedContainerListener();
        manager.listen(this, AnyFilter.getInstance(), l);
        assertThat(manager.isClean()).isFalse();
        l = null;

        TestUtils.forceGC();

        manager.emit(Events.NULL, eventType, null);
        assertThat(manager.isClean()).isTrue();
    }

    @ManagedBy(ManagementType.MANUAL)
    private static class ManualManagedListener extends SimpleAdapter {
        @Override
        public boolean onEvent(Object emitter, Object type, Object data) {
            return true;
        }
    }

    @Test
    public void testManagerShouldntCleanupMethodAdapterIfObjectManagedManual() throws Exception {

        ManualManagedListener l = new ManualManagedListener();
        final MethodAdapter methodAdapter = new MethodAdapter(l, "onEvent");
        manager.listen(this, AnyFilter.getInstance(), methodAdapter);
        l = null;

        TestUtils.forceGC();

        manager.emit(Events.NULL, eventType, null);
        assertThat(manager.isClean()).isFalse();

    }

    @Test
    public void testManagerCleanupsDataIfEmitterNoMoreReachableByStrongRef() throws Exception {

        byte[] e = new byte[1024];
        manager.listen(e, AnyFilter.getInstance(), listener);
        assertThat(manager.isClean()).isFalse();
        e = null;

        TestUtils.forceGC();

        // to force cleanup of stales
        manager.emit(Events.NULL, eventType, null);
        assertThat(manager.isClean()).isTrue();
    }

    @Test
    public void testManagerHoldsListenerByStrongRef() throws Exception {
        EventListener<Object,Object,Object> l = new SimpleAdapter<Object, Object, Object>() {
            @Override
            public boolean onEvent(Object emitter, Object type, Object data) {
                return true;
            }
        };
        manager.listen(this, AnyFilter.getInstance(), l);
        assertThat(manager.isClean()).isFalse();

        l = null;
        TestUtils.forceGC();

        // to force cleanup of stales
        manager.emit(Events.NULL, eventType, null);
        assertThat(manager.isClean()).isFalse();
    }

    @Test
    public void testMuteCorrectlyRemovesListener() throws Exception {

        manager.listen(this, AnyFilter.getInstance(), listener);
        manager.listen(this, AnyFilter.getInstance(), listener2);

        assertThat(manager.isClean()).isFalse();
        manager.mute(listener2);

        Assertions.assertThat((Object) manager.list.iterator().next().listenerProvider.get()).isEqualTo(listener);
        manager.mute(listener);

        assertThat(manager.isClean()).isTrue();
    }

    @Test
    public void testMuteCorrectlyRemovesByCounterpart() throws Exception {
        manager.listen(this, AnyFilter.getInstance(), new MethodAdapter(listener, "equals"));
        manager.mute(listener);
        assertThat(manager.isClean()).isTrue();
    }

    @Test
    public void testManagerCanCreateRegistrar() throws Exception {
        final Registrar registrar = manager.createRegistrar();
        Assertions.assertThat(registrar).isNotNull();
    }

    @Test
    public void testRegistrarHoldsStateByWeakReferences() throws Exception {

        ManagedContainerListener l = new ManagedContainerListener();
        final Registrar registrar = manager.createRegistrar();
        registrar.listen(this, AnyFilter.getInstance(), l);
        manager.mute(l);
        l = null;

        TestUtils.forceGC();

        assertThat(registrar.isClean()).isTrue();
        assertThat(manager.isClean()).isTrue();
    }

    @Test
    public void testRegistrarRegisterListeners() throws Exception {
        final Registrar registrar = manager.createRegistrar();
        registrar.listen(this, AnyFilter.getInstance(), listener);
        registrar.listen(this, AnyFilter.getInstance(), listener2);
        verifyBothCalled();
    }

    @Test
    public void testRegistrarCleanupListeners() throws Exception {
        final Registrar registrar = manager.createRegistrar();
        registrar.listen(this, AnyFilter.getInstance(), listener);
        registrar.listen(this, AnyFilter.getInstance(), listener2);
        registrar.cleanup();
        verifyNoneCalled();
    }

    @Test
    public void testRegistrarRemoveListeners() throws Exception {
        final Registrar registrar = manager.createRegistrar();
        registrar.listen(this, AnyFilter.getInstance(), listener);
        registrar.listen(this, AnyFilter.getInstance(), listener2);
        registrar.mute(listener2);
        verifyOnlyFirstCalled();
    }

    @Test
    public void testRegisterCanRemoveOnlyRegisteredListeners() throws Exception {
        final Registrar registrar = manager.createRegistrar();
        registrar.listen(this, AnyFilter.getInstance(), listener2);
        manager.listen(this, AnyFilter.getInstance(), listener);
        registrar.mute(listener);
        registrar.mute(listener2);
        verifyOnlyFirstCalled();
    }

    @Test
    public void testRegistrarCleanupOnlyRegisteredListeners() throws Exception {
        final Registrar registrar = manager.createRegistrar();
        registrar.listen(this, AnyFilter.getInstance(), listener2);
        manager.listen(this, AnyFilter.getInstance(), listener);
        registrar.cleanup();
        verifyOnlyFirstCalled();
    }

    @Test
    public void testManagerShouldCleanupMethodAdapterIfObjectNoMoreReachable() throws Exception {

        Object l = new Object();

        final MethodAdapter methodAdapter = new MethodAdapter(l, "hashCode");
        manager.listen(this, AnyFilter.getInstance(), methodAdapter);

        l = null;
        TestUtils.forceGC();

        manager.emit(Events.NULL, eventType, null);
        assertThat(manager.isClean()).isTrue();
    }

    @Test
    public void testCleanupRemovesAllListeners() throws Exception {

        manager.listen(this, AnyFilter.getInstance(), listener);
        manager.listen(this, AnyFilter.getInstance(), listener2);
        assertThat(manager.isClean()).isFalse();

        manager.cleanup();
        assertThat(manager.isClean()).isTrue();

    }

    private void verifyOnlyFirstCalled() {
        manager.emit(this, eventType, null);
        Mockito.verify(listener).onEvent(this, eventType, null);
        Mockito.verify(listener2, Mockito.never()).onEvent(Matchers.any(), Matchers.any(), Matchers.any());
    }

    private void verifyBothCalled() {
        manager.emit(this, eventType, null);
        Mockito.verify(listener).onEvent(this, eventType, null);
        Mockito.verify(listener2).onEvent(this, eventType, null);
    }

    private void verifyNoneCalled() {
        manager.emit(this, eventType, null);
        Mockito.verify(listener, Mockito.never()).onEvent(Matchers.any(), Matchers.any(), Matchers.any());
        Mockito.verify(listener2, Mockito.never()).onEvent(Matchers.any(), Matchers.any(), Matchers.any());
    }


}
