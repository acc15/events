package ru.vmsoftware.events.listeners.adapters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.vmsoftware.events.TestData;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
public class MethodAdapterTest {

    interface Listener {

        void listener();

        boolean listenerResult();

        void listener(String data);

        boolean listenerResult(String data);

        void listener(String eventType, String data);

        boolean listenerResult(String eventType, String data);

        void listener(String emitter, String eventType, String data);

        boolean listenerResult(String emitter, String eventType, String data);

    }


    @Mock
    private Listener listener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMethodAdapterFindsMethodCorrectly() throws Exception {
        MethodAdapter a = new MethodAdapter(listener, "listener");
        a.handleEvent(TestData.EMITTER, TestData.TYPE, TestData.DATA);
        verify(listener).listener(TestData.EMITTER, TestData.TYPE, TestData.DATA);
        verifyNoMoreInteractions(listener);

        final boolean result = false;
        when(listener.listenerResult(TestData.EMITTER, TestData.TYPE, TestData.DATA)).thenReturn(result);

        MethodAdapter b = new MethodAdapter(listener, "listenerResult");
        assertThat(b.handleEvent(TestData.EMITTER, TestData.TYPE, TestData.DATA)).isEqualTo(result);
        verify(listener).listenerResult(TestData.EMITTER, TestData.TYPE, TestData.DATA);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testMethodAdapterCorrectlyCallsMethod() throws Exception {

        final boolean result = false;

        MethodAdapter a = new MethodAdapter(listener,
                listener.getClass().getMethod("listener"));
        a.handleEvent(TestData.EMITTER, TestData.TYPE, TestData.DATA);
        verify(listener).listener();
        verifyNoMoreInteractions(listener);

        MethodAdapter b = new MethodAdapter(listener,
                listener.getClass().getMethod("listenerResult"));
        when(listener.listenerResult()).thenReturn(result);
        assertThat(b.handleEvent(TestData.EMITTER, TestData.TYPE, TestData.DATA)).isEqualTo(result);
        verify(listener).listenerResult();
        verifyNoMoreInteractions(listener);

        MethodAdapter c = new MethodAdapter(listener,
                listener.getClass().getMethod("listener", String.class));
        c.handleEvent(TestData.EMITTER, TestData.TYPE, TestData.DATA);
        verify(listener).listener(TestData.DATA);
        verifyNoMoreInteractions(listener);

        MethodAdapter d = new MethodAdapter(listener,
                listener.getClass().getMethod("listenerResult", String.class));
        when(listener.listenerResult(TestData.DATA)).thenReturn(result);
        assertThat(d.handleEvent(TestData.EMITTER, TestData.TYPE, TestData.DATA)).isEqualTo(result);
        verify(listener).listenerResult(TestData.DATA);
        verifyNoMoreInteractions(listener);

        MethodAdapter e = new MethodAdapter(listener,
                listener.getClass().getMethod("listener", String.class, String.class));
        e.handleEvent(TestData.EMITTER, TestData.TYPE, TestData.DATA);
        verify(listener).listener(TestData.TYPE, TestData.DATA);
        verifyNoMoreInteractions(listener);

        MethodAdapter f = new MethodAdapter(listener,
                listener.getClass().getMethod("listenerResult", String.class, String.class));
        when(listener.listenerResult(TestData.TYPE, TestData.DATA)).thenReturn(result);
        assertThat(f.handleEvent(TestData.EMITTER, TestData.TYPE, TestData.DATA)).isEqualTo(result);
        verify(listener).listenerResult(TestData.TYPE, TestData.DATA);
        verifyNoMoreInteractions(listener);

        MethodAdapter g = new MethodAdapter(listener,
                listener.getClass().getMethod("listener", String.class, String.class, String.class));
        g.handleEvent(TestData.EMITTER, TestData.TYPE, TestData.DATA);
        verify(listener).listener(TestData.EMITTER, TestData.TYPE, TestData.DATA);
        verifyNoMoreInteractions(listener);

        MethodAdapter h = new MethodAdapter(listener,
                listener.getClass().getMethod("listenerResult", String.class, String.class, String.class));
        when(listener.listenerResult(TestData.EMITTER, TestData.TYPE, TestData.DATA)).thenReturn(result);
        assertThat(h.handleEvent(TestData.EMITTER, TestData.TYPE, TestData.DATA)).isEqualTo(result);
        verify(listener).listenerResult(TestData.EMITTER, TestData.TYPE, TestData.DATA);
        verifyNoMoreInteractions(listener);

    }

}
