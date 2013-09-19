package ru.vmsoftware.events;

import ru.vmsoftware.events.listeners.EventListener;

import java.util.Arrays;
import java.util.List;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-19-09
 */
public class TestData {
    public static final String EMITTER = "emitter";
    public static final String TYPE = "type";
    public static final String DATA = "data";

    public static final List<Object> PARAMETERS = Arrays.<Object>asList(EMITTER, TYPE, DATA);

    public static boolean handleEventWithTestData(EventListener<Object,Object,Object> l) {
        return l.handleEvent(EMITTER, TYPE, DATA);
    }

}
