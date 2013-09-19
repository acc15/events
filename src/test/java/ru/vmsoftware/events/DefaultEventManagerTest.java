package ru.vmsoftware.events;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.vmsoftware.events.adapters.ListenerAdapter;
import ru.vmsoftware.events.adapters.MethodAdapter;
import ru.vmsoftware.events.annotations.ManagedBy;
import ru.vmsoftware.events.filters.Filter;
import ru.vmsoftware.events.filters.Filters;
import ru.vmsoftware.events.references.ManagementType;

import java.io.Serializable;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
public class DefaultEventManagerTest implements Serializable {

    @Mock
    private EventListener<Object, Object, Object> listener;

    @Mock
    private EventListener<Object, Object, Object> listener2;


    private Object eventType = new Object();
    private Object eventData = new Object();

    private DefaultEventManager manager = new DefaultEventManager();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(listener.onEvent(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(listener2.onEvent(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
    }

    @Test(expected = NullPointerException.class)
    public void testListenDoesntAcceptNullPointersForEmitter() throws Exception {
        manager.listen(null, Filters.any(), listener);
    }

    @Test(expected = NullPointerException.class)
    public void testListenDoesntAcceptNullPointersForType() throws Exception {
        manager.listen(this, null, listener);
    }

    @Test(expected = NullPointerException.class)
    public void testListenDoesntAcceptNullPointersForListener2() throws Exception {
        manager.listen(this, Filters.any(), null);
    }

    @Test(expected = NullPointerException.class)
    public void testEmitDoesntAcceptNullPointersForEmitter() throws Exception {
        manager.emit(null, eventType, eventData);
    }

    @Test(expected = NullPointerException.class)
    public void testEmitDoesntAcceptNullPointersForType() throws Exception {
        manager.emit(this, null, eventData);
    }

    @Test
    public void testEmitAcceptNullPointersForData() throws Exception {
        manager.emit(this, eventType, null);
    }

    @Test
    public void testEmittedEventDeliveredCorrectly() throws Exception {
        manager.listen(this, Filters.any(), listener);
        manager.emit(this, eventType, eventData);
        verify(listener).onEvent(this, eventType, eventData);
    }

    @Test
    public void testEmitShouldCallListenersRegisteredWithInterfaceOrParentClass() throws Exception {
        manager.listen(Serializable.class, Filters.any(), listener);
        manager.emit(this, eventType, eventData);
        verify(listener).onEvent(this, eventType, eventData);
    }

    @Test
    public void testEmittedEventsDeliveredIfListenerAddForEmitterClass() throws Exception {
        manager.listen(DefaultEventManagerTest.class, Filters.any(), listener);
        manager.emit(this, eventType, eventData);
        verify(listener).onEvent(this, eventType, eventData);
    }

    @Test
    public void testManagerDoesntCallListenerIfEventTypeNotMatched() throws Exception {
        manager.listen(this, Filters.equalTo(eventType), listener);
        manager.listen(this, Filters.equalTo("xyz"), listener2);
        verifyOnlyFirstCalled();
    }

    @Test
    public void testManagerDoesntCallListenerIfEmitterNotMatched() throws Exception {
        manager.listen(this, Filters.any(), listener);
        manager.listen(Filter.class, Filters.any(), listener2);
        verifyOnlyFirstCalled();
    }

    @Test
    public void testManagerBreaksExecutionIfSomeoneListenerReturnsFalse() throws Exception {
        Mockito.when(listener.onEvent(this, eventType, eventData)).thenReturn(false);

        manager.listen(this, Filters.any(), listener);
        manager.listen(this, Filters.any(), listener2);
        manager.emit(this, eventType, eventData);

        verify(listener).onEvent(this, eventType, eventData);
        verify(listener2, never()).onEvent(Matchers.any(), Matchers.any(), Matchers.any());
    }

    @ManagedBy(ManagementType.CONTAINER)
    private static class ManagedContainerListener extends ListenerAdapter {
        public boolean onEvent(Object emitter, Object type, Object event) {
            return true;
        }
    }

    @Test
    public void testManagerCleanupsDataIfListenerIsManagedByContainer() throws Exception {
        ManagedContainerListener l = new ManagedContainerListener();
        manager.listen(this, Filters.any(), l);
        assertThat(manager.isClean()).isFalse();
        l = null;

        TestUtils.forceGC();

        manager.emit(TestUtils.NULL, eventType, null);
        assertThat(manager.isClean()).isTrue();
    }

    @ManagedBy(ManagementType.MANUAL)
    private static class ManualManagedListener extends ListenerAdapter {
        public boolean onEvent(Object emitter, Object type, Object event) {
            return true;
        }
    }

    @Test
    public void testManagerShouldntCleanupMethodAdapterIfObjectManagedManual() throws Exception {

        ManualManagedListener l = new ManualManagedListener();
        final MethodAdapter methodAdapter = new MethodAdapter(l, "onEvent");
        manager.listen(this, Filters.any(), methodAdapter);
        l = null;

        TestUtils.forceGC();

        manager.emit(TestUtils.NULL, eventType, null);
        assertThat(manager.isClean()).isFalse();

    }

    @Test
    public void testManagerCleanupsDataIfEmitterNoMoreReachableByStrongRef() throws Exception {

        byte[] e = new byte[1024];
        manager.listen(e, Filters.any(), listener);
        assertThat(manager.isClean()).isFalse();
        e = null;

        TestUtils.forceGC();

        // to force cleanup of stales
        manager.emit(TestUtils.NULL, eventType, null);
        assertThat(manager.isClean()).isTrue();
    }

    @Test
    public void testManagerHoldsListenerByStrongRef() throws Exception {
        EventListener<Object, Object, Object> l = new ListenerAdapter<Object, Object, Object>() {
            public boolean onEvent(Object emitter, Object type, Object event) {
                return true;
            }
        };
        manager.listen(this, Filters.any(), l);
        assertThat(manager.isClean()).isFalse();

        l = null;
        TestUtils.forceGC();

        // to force cleanup of stales
        manager.emit(TestUtils.NULL, eventType, null);
        assertThat(manager.isClean()).isFalse();
    }

    @Test
    public void testMuteCorrectlyRemovesListener() throws Exception {

        manager.listen(this, Filters.any(), listener);
        manager.listen(this, Filters.any(), listener2);

        assertThat(manager.isClean()).isFalse();
        manager.mute(listener2);

        Assertions.assertThat((Object) manager.list.iterator().next().listenerProvider.get()).isEqualTo(listener);
        manager.mute(listener);

        assertThat(manager.isClean()).isTrue();
    }

    @Test
    public void testMuteCorrectlyRemovesByCounterpart() throws Exception {
        manager.listen(this, Filters.any(), new MethodAdapter(listener, "equals"));
        manager.mute(listener);
        assertThat(manager.isClean()).isTrue();
    }

    @Test
    public void testManagerCanCreateRegistrar() throws Exception {
        final Registrar registrar = manager.registrar();
        Assertions.assertThat(registrar).isNotNull();
    }

    @Test
    public void testCreateEmitterReturnsObjectForEmitting() throws Exception {

        manager.listen(this, eventType, listener);

        final Emitter emitter = manager.emitter(this);
        emitter.emit(eventType);
        verify(listener).onEvent(this, eventType, null);

        emitter.emit(eventType, eventData);
        verify(listener).onEvent(this, eventType, eventData);
    }

    @Test
    public void testRegistrarHoldsStateByWeakReferences() throws Exception {

        ManagedContainerListener l = new ManagedContainerListener();
        final Registrar registrar = manager.registrar();
        registrar.listen(this, Filters.any(), l);
        manager.mute(l);
        l = null;

        TestUtils.forceGC();

        assertThat(registrar.isClean()).isTrue();
        assertThat(manager.isClean()).isTrue();
    }

    @Test
    public void testRegistrarRegisterListeners() throws Exception {
        final Registrar registrar = manager.registrar();
        registrar.listen(this, Filters.any(), listener);
        registrar.listen(this, Filters.any(), listener2);
        verifyBothCalled();
    }

    @Test
    public void testRegistrarCleanupListeners() throws Exception {
        final Registrar registrar = manager.registrar();
        registrar.listen(this, Filters.any(), listener);
        registrar.listen(this, Filters.any(), listener2);
        registrar.cleanup();
        verifyNoneCalled();
    }

    @Test
    public void testRegistrarRemoveListeners() throws Exception {
        final Registrar registrar = manager.registrar();
        registrar.listen(this, Filters.any(), listener);
        registrar.listen(this, Filters.any(), listener2);
        registrar.mute(listener2);
        verifyOnlyFirstCalled();
    }

    @Test
    public void testRegisterCanRemoveOnlyRegisteredListeners() throws Exception {
        final Registrar registrar = manager.registrar();
        registrar.listen(this, Filters.any(), listener2);
        manager.listen(this, Filters.any(), listener);
        registrar.mute(listener);
        registrar.mute(listener2);
        verifyOnlyFirstCalled();
    }

    @Test
    public void testRegistrarCleanupOnlyRegisteredListeners() throws Exception {
        final Registrar registrar = manager.registrar();
        registrar.listen(this, Filters.any(), listener2);
        manager.listen(this, Filters.any(), listener);
        registrar.cleanup();
        verifyOnlyFirstCalled();
    }

    @Test
    public void testManagerShouldCleanupMethodAdapterIfObjectNoMoreReachable() throws Exception {

        Object l = new Object();

        final MethodAdapter methodAdapter = new MethodAdapter(l, "hashCode");
        manager.listen(this, Filters.any(), methodAdapter);

        l = null;
        TestUtils.forceGC();

        manager.emit(TestUtils.NULL, eventType, null);
        assertThat(manager.isClean()).isTrue();
    }

    @Test
    public void testCleanupRemovesAllListeners() throws Exception {

        manager.listen(this, Filters.any(), listener);
        manager.listen(this, Filters.any(), listener2);
        assertThat(manager.isClean()).isFalse();

        manager.cleanup();
        assertThat(manager.isClean()).isTrue();

    }

    private void verifyOnlyFirstCalled() {
        manager.emit(this, eventType, eventData);
        verify(listener).onEvent(this, eventType, eventData);
        verify(listener2, never()).onEvent(Matchers.any(), Matchers.any(), Matchers.any());
    }

    private void verifyBothCalled() {
        manager.emit(this, eventType, eventData);
        verify(listener).onEvent(this, eventType, eventData);
        verify(listener2).onEvent(this, eventType, eventData);
    }

    private void verifyNoneCalled() {
        manager.emit(this, eventType, eventData);
        verify(listener, never()).onEvent(Matchers.any(), Matchers.any(), Matchers.any());
        verify(listener2, never()).onEvent(Matchers.any(), Matchers.any(), Matchers.any());
    }


}
