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
        public boolean filter(Object value) {
            return true;
        }
    };
    private EventListener listener = new SimpleAdapter() {
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
    public void testEmitCorrectlyRedirectsEmitterAndType() throws Exception {
        Events.emit(emitter, type);
        verify(eventManager).emit(emitter, type);
    }

    @Test
    public void testEmitCorrectlyRedirectEmitterTypeAndData() throws Exception {
        Events.emit(emitter, type, data);
        verify(eventManager).emit(emitter, type, data);
    }

    @Test
    public void testListenFilterCorrectlyRedirectsEmitterTypeAndListener() throws Exception {
        Events.listen(emitter, type, listener);
        verify(eventManager).listen(emitter, type, listener);
    }
}
