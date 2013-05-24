package ru.vmsoftware.events.filters;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * @author Vyacheslav Mayorov
 * @since 2013-03-05
 */
public class InstanceOfFilterTest {
    @Test
    public void testFilter() throws Exception {

        final Filter<Object> filter = new InstanceOfFilter<Object>(CharSequence.class);
        assertThat(filter.filter("abc")).isTrue();
        assertThat(filter.filter(new StringBuilder())).isTrue();
        assertThat(filter.filter(123)).isFalse();

    }
}
