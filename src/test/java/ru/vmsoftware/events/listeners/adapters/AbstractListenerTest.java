package ru.vmsoftware.events.listeners.adapters;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
public class AbstractListenerTest {

    private static class TestListener extends AbstractListener<Object,Object,Object> {
        public boolean isCalled() {
            return called;
        }

        private Object getEmitter() {
            return emitter;
        }

        private Object getType() {
            return type;
        }

        private Object getData() {
            return data;
        }

        public void setCallData(Object emitter, Object type, Object data) {
            this.emitter = emitter;
            this.type = type;
            this.data = data;
            this.called = true;
        }

        private Object emitter;
        private Object type;
        private Object data;
        private boolean called = false;
    }

    private Object emitter = new Object();
    private Object type = new Object();
    private Object data = new Object();

    private static <T> List<T> listUntilNulls(T... values) {
        final List<T> l = new ArrayList<T>();
        for (T v: values) {
            if (v == null) {
                break;
            }
            l.add(v);
        }
        return l;
    }

    public void testListener(TestListener l, int paramCount) {
        assertThat(l.handleEvent(emitter, type, data));
        assertThat(l.isCalled()).as("listener with " + paramCount + " parameters").isTrue();
        final List<Object> expectedParams = Arrays.asList(data, type, emitter).subList(0, paramCount);
        final List<Object> actualParams = listUntilNulls(l.getData(), l.getType(), l.getEmitter());
        assertThat(actualParams).isEqualTo(expectedParams);
    }

    @Test
    public void testNoArgOnEventCalled() throws Exception {
        testListener(new TestListener() {
            @Override
            public void onEvent() {
                setCallData(null, null, null);
            }
        }, 0);
    }

    @Test
    public void testDataEventCalled() throws Exception {
        testListener(new TestListener() {
            @Override
            public void onEvent(Object data) {
                setCallData(null, null, data);
            }
        }, 1);
    }

    @Test
    public void testTypeEventCalled() throws Exception {
        testListener(new TestListener() {
            @Override
            public void onEvent(Object type, Object data) {
                setCallData(null, type, data);
            }
        }, 2);
    }

    @Test
    public void testSimpleEventCalled() throws Exception {
        testListener(new TestListener() {
            @Override
            public void onEvent(Object emitter, Object type, Object data) {
                setCallData(emitter, type, data);
            }
        }, 3);
    }
}
