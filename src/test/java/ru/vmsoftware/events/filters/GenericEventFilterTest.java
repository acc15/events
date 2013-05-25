package ru.vmsoftware.events.filters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.vmsoftware.events.GenericEvent;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-25-05
 */
public class GenericEventFilterTest {

    @Mock
    private Filter<Object> emitterFilter;

    @Mock
    private Filter<Object> typeFilter;

    private GenericEventFilter genericEventFilter;

    final GenericEvent<Object,Object,Object> event = new GenericEvent<Object, Object, Object>(
            this, this
    );

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        genericEventFilter = new GenericEventFilter(emitterFilter, typeFilter);
    }

    @Test
    public void testFilterShouldNotFilterEventsOfDifferentClass() throws Exception {
        assertThat(genericEventFilter.filter("abc")).isFalse();
        assertThat(genericEventFilter.filter(50)).isFalse();

    }

    @Test
    public void testFilterShouldFilterWhenEmitterAndTypeMatches() throws Exception {
        when(emitterFilter.filter(this)).thenReturn(true);
        when(typeFilter.filter(this)).thenReturn(true);
        assertThat(genericEventFilter.filter(event)).isTrue();
        verify(emitterFilter).filter(this);
        verify(typeFilter).filter(this);
    }

    @Test
    public void testFilterShouldNotFilterWhenTypeNotMatches() throws Exception {
        when(emitterFilter.filter(this)).thenReturn(true);
        when(typeFilter.filter(this)).thenReturn(false);
        assertThat(genericEventFilter.filter(event)).isFalse();
        verify(emitterFilter).filter(this);
        verify(typeFilter).filter(this);
    }

    @Test
    public void testFilterShouldNotFilterWhenEmiiterNotMatches() throws Exception {
        when(emitterFilter.filter(this)).thenReturn(false);
        assertThat(genericEventFilter.filter(event)).isFalse();
        verify(emitterFilter).filter(this);
        verify(typeFilter, Mockito.never()).filter(this);
    }

    @Test
    public void testFilterShouldReturnUnderlyingFilters() throws Exception {
        assertThat(genericEventFilter.getUnderlyingObjects()).containsExactly(emitterFilter, typeFilter);
    }
}
