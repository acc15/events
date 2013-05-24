package ru.vmsoftware.events.filters;

import ru.vmsoftware.events.GenericEvent;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-25-05
 */
public class GenericEventFilter implements Filter<Object> {

    @SuppressWarnings("unchecked")
    public GenericEventFilter(Filter<?> emitterFilter, Filter<?> typeFilter) {
        this.emitterFilter = (Filter<Object>) emitterFilter;
        this.typeFilter = (Filter<Object>) typeFilter;
    }

    @Override
    public boolean filter(Object value) {
        if (!(value instanceof GenericEvent)) {
            return false;
        }
        final GenericEvent event = (GenericEvent) value;
        return emitterFilter.filter(event.getEmitter()) && typeFilter.filter(event.getType());
    }


    private Filter<Object> emitterFilter;
    private Filter<Object> typeFilter;
}
