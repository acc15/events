package ru.vmsoftware.events;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.vmsoftware.events.adapters.SimpleAdapter;
import ru.vmsoftware.events.filters.Filter;

import static org.fest.assertions.api.Assertions.assertThat;


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
    private Filter<Object> filter = new Filter<Object>() {
        @Override
        public boolean filter(Object value) {
            return true;
        }
    };
    private EventListener<Object, Object, Object> listener = new SimpleAdapter<Object, Object, Object>() {
        @Override
        public boolean onEvent(Object emitter, Object type, Object data) {
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
        Events.emit(emitter, type);
        Mockito.verify(eventManager).emit(emitter, type, null);
    }

    @Test
    public void testEmit2CorrectlyDelegatesCall() throws Exception {
        Events.emit(emitter, type, data);
        Mockito.verify(eventManager).emit(emitter, type, data);
    }

    @Test
    public void testListenFilterCorrectlyDelegatesCall() throws Exception {
        Events.listen(emitter, filter, listener);
        Mockito.verify(eventManager).listen(emitter, filter, listener);
    }
}
