package ru.vmsoftware.events.listeners.adapters;

import org.junit.Test;
import ru.vmsoftware.events.TestData;
import ru.vmsoftware.events.TestUtils;
import ru.vmsoftware.events.listeners.*;
import ru.vmsoftware.events.providers.Provider;
import ru.vmsoftware.events.references.ManagementType;
import ru.vmsoftware.events.references.ReferenceInitializer;
import ru.vmsoftware.events.references.ReferenceManager;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
public class ListenerAdapterTest {

    private static class TestListener implements SimpleListener<Object,Object,Object>,
            TypeListener<Object,Object>,
            DataListener<Object>,
            NoArgListener {
        public void onEvent(Object emitter, Object type, Object data) {
            callRecorder.recordCall(emitter, type, data);
        }

        public void onEvent(Object type, Object data) {
            callRecorder.recordCall(type, data);
        }

        public void onEvent(Object data) {
            callRecorder.recordCall(data);
        }

        public void onEvent() {
            callRecorder.recordCall();
        }

        private TestUtils.CallRecorder getCallRecorder() {
            return callRecorder;
        }

        private TestUtils.CallRecorder callRecorder = new TestUtils.CallRecorder();
    }

    private void testAdapter(final TestListener l, EventListener<Object,Object,Object> eventListener, int paramCount) {
        TestData.handleEventWithTestData(eventListener);
        assertThat(l.getCallRecorder().getCallArgs()).
                isEqualTo(TestUtils.tail(TestData.PARAMETERS, paramCount));
        assertThat(eventListener.isCounterpart(l)).isTrue();
        assertThat(eventListener).isInstanceOf(ReferenceInitializer.class);
        ((ReferenceInitializer)eventListener).initReferences(new ReferenceManager() {
            public <T> Provider<T> manage(Provider<T> provider, ManagementType defaultType) {
                return manage(provider);
            }

            public <T> Provider<T> manage(Provider<T> provider) {
                assertThat(provider.get()).isSameAs((T)l);
                return provider;
            }
        });
    }

    @Test
    public void testSimpleListenerAdapter() throws Exception {
        final TestListener l = new TestListener();
        testAdapter(l, new SimpleListenerAdapter<Object, Object, Object>(l), 3);
    }

    @Test
    public void testTypeListenerAdapter() throws Exception {
        final TestListener l = new TestListener();
        testAdapter(l, new TypeListenerAdapter<Object, Object>(l), 2);
    }

    @Test
    public void testDataListenerAdapter() throws Exception {
        final TestListener l = new TestListener();
        testAdapter(l, new DataListenerAdapter<Object>(l), 1);
    }

    @Test
    public void testNoArgListenerAdapter() throws Exception {
        final TestListener l = new TestListener();
        testAdapter(l, new NoArgListenerAdapter(l), 0);
    }
}
