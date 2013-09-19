package ru.vmsoftware.events.listeners.adapters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
public class MethodAdapterTest {

    private static final String EMITTER = "emitter";
    private static final String TYPE = "type";
    private static final String DATA = "data";

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
        a.onEvent(EMITTER, TYPE, DATA);
        verify(listener).listener(EMITTER, TYPE, DATA);
        verifyNoMoreInteractions(listener);

        final boolean result = false;
        when(listener.listenerResult(EMITTER, TYPE, DATA)).thenReturn(result);

        MethodAdapter b = new MethodAdapter(listener, "listenerResult");
        assertThat(b.onEvent(EMITTER, TYPE, DATA)).isEqualTo(result);
        verify(listener).listenerResult(EMITTER, TYPE, DATA);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testMethodAdapterCorrectlyCallsMethod() throws Exception {

        final boolean result = false;

        MethodAdapter a = new MethodAdapter(listener,
                listener.getClass().getMethod("listener"));
        a.onEvent(EMITTER, TYPE, DATA);
        verify(listener).listener();
        verifyNoMoreInteractions(listener);

        MethodAdapter b = new MethodAdapter(listener,
                listener.getClass().getMethod("listenerResult"));
        when(listener.listenerResult()).thenReturn(result);
        assertThat(b.onEvent(EMITTER, TYPE, DATA)).isEqualTo(result);
        verify(listener).listenerResult();
        verifyNoMoreInteractions(listener);

        MethodAdapter c = new MethodAdapter(listener,
                listener.getClass().getMethod("listener", String.class));
        c.onEvent(EMITTER, TYPE, DATA);
        verify(listener).listener(DATA);
        verifyNoMoreInteractions(listener);

        MethodAdapter d = new MethodAdapter(listener,
                listener.getClass().getMethod("listenerResult", String.class));
        when(listener.listenerResult(DATA)).thenReturn(result);
        assertThat(d.onEvent(EMITTER, TYPE, DATA)).isEqualTo(result);
        verify(listener).listenerResult(DATA);
        verifyNoMoreInteractions(listener);

        MethodAdapter e = new MethodAdapter(listener,
                listener.getClass().getMethod("listener", String.class, String.class));
        e.onEvent(EMITTER, TYPE, DATA);
        verify(listener).listener(TYPE, DATA);
        verifyNoMoreInteractions(listener);

        MethodAdapter f = new MethodAdapter(listener,
                listener.getClass().getMethod("listenerResult", String.class, String.class));
        when(listener.listenerResult(TYPE, DATA)).thenReturn(result);
        assertThat(f.onEvent(EMITTER, TYPE, DATA)).isEqualTo(result);
        verify(listener).listenerResult(TYPE, DATA);
        verifyNoMoreInteractions(listener);

        MethodAdapter g = new MethodAdapter(listener,
                listener.getClass().getMethod("listener", String.class, String.class, String.class));
        g.onEvent(EMITTER, TYPE, DATA);
        verify(listener).listener(EMITTER, TYPE, DATA);
        verifyNoMoreInteractions(listener);

        MethodAdapter h = new MethodAdapter(listener,
                listener.getClass().getMethod("listenerResult", String.class, String.class, String.class));
        when(listener.listenerResult(EMITTER, TYPE, DATA)).thenReturn(result);
        assertThat(h.onEvent(EMITTER, TYPE, DATA)).isEqualTo(result);
        verify(listener).listenerResult(EMITTER, TYPE, DATA);
        verifyNoMoreInteractions(listener);

    }

}
