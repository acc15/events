package ru.vmsoftware.events.listeners.adapters;

import org.junit.Test;
import ru.vmsoftware.events.TestData;
import ru.vmsoftware.events.TestUtils;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
public class AbstractListenerTest {

    private static class TestListener extends AbstractListener<Object,Object,Object> {

        public TestUtils.CallRecorder getCallRecorder() {
            return callRecorder;
        }

        private TestUtils.CallRecorder callRecorder = new TestUtils.CallRecorder();
    }

    public void testListener(TestListener l, int paramCount) {
        assertThat(TestData.handleEventWithTestData(l));
        assertThat(l.getCallRecorder().isCalled()).as("listener with " + paramCount + " parameters").isTrue();
        final List<Object> expectedParams = TestUtils.tail(TestData.PARAMETERS, paramCount);
        assertThat(l.getCallRecorder().getCallArgs()).isEqualTo(expectedParams);
    }

    @Test
    public void testNoArgOnEventCalled() throws Exception {
        testListener(new TestListener() {
            @Override
            public void onEvent() {
                getCallRecorder().recordCall();
            }
        }, 0);
    }

    @Test
    public void testDataEventCalled() throws Exception {
        testListener(new TestListener() {
            @Override
            public void onEvent(Object data) {
                getCallRecorder().recordCall(data);
            }
        }, 1);
    }

    @Test
    public void testTypeEventCalled() throws Exception {
        testListener(new TestListener() {
            @Override
            public void onEvent(Object type, Object data) {
                getCallRecorder().recordCall(type, data);
            }
        }, 2);
    }

    @Test
    public void testSimpleEventCalled() throws Exception {
        testListener(new TestListener() {
            @Override
            public void onEvent(Object emitter, Object type, Object data) {
                getCallRecorder().recordCall(emitter, type, data);
            }
        }, 3);
    }
}
