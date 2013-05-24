package ru.vmsoftware.events.filters;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
public class AnyFilterTest {
    @Test
    public void testFilter() throws Exception {

        final AnyFilter<String> filter = AnyFilter.getInstance();
        assertThat(filter.filter("abc")).isTrue();
        assertThat(filter.filter("xyz")).isTrue();

    }
}
