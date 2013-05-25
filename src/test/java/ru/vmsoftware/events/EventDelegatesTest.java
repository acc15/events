package ru.vmsoftware.events;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.vmsoftware.events.adapters.SimpleAdapter;
import ru.vmsoftware.events.filters.AbstractSimpleFilter;
import ru.vmsoftware.events.filters.Filter;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;


/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
public class EventDelegatesTest {

    @Mock
    private EventManager eventManager;
    private EventManager restoreManager;

    private Object emitter = new Object();
    private Object type = new Object();
    private Object data = new Object();
    private Filter<Object> filter = new AbstractSimpleFilter<Object>() {
        @Override
        public boolean filter(Object value) {
            return true;
        }
    };
    private EventListener<Object> listener = new SimpleAdapter<Object>() {
        @Override
        public boolean onEvent(Object event) {
            return true;
        }
    };

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        restoreManager = Events.manager;
        Events.manager = eventManager;
    }

    @After
    public void tearDown() throws Exception {
        Events.manager = restoreManager;
    }

    @Test
    public void testGetManager() throws Exception {
        assertThat(Events.getManager()).isSameAs(eventManager);
    }

    @Test
    public void testEmitCorrectlyDelegatesCall() throws Exception {
        Events.emit(data);
        verify(eventManager).emit(data);
    }

    @Test
    public void testEmit2CorrectlyDelegatesCall() throws Exception {
        Events.emit(emitter, type);
        verify(eventManager).emit(emitter, type);
    }

    @Test
    public void testEmit3CorrectlyDelegatesCall() throws Exception {
        Events.emit(emitter, type, data);
        verify(eventManager).emit(emitter, type, data);
    }

    @Test
    public void testListenCorrectlyDelegatesCall() throws Exception {
        Events.listen(filter, listener);
        verify(eventManager).listen(filter, listener);
    }

    @Test
    public void testListenFilterCorrectlyDelegatesCall() throws Exception {
        Events.listen(emitter, filter, listener);
        verify(eventManager).listen(emitter, filter, listener);
    }
}
