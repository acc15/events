package ru.vmsoftware.events.filters;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-08-05
 */
public class AndFilterTest {
    @Test
    public void testFilter() throws Exception {
        final String val = "abc";
        final Filter<Object> f = new AndFilter<Object>(
                new InstanceOfFilter<Object>(String.class),
                new SameInstanceFilter<Object>(val));
        assertThat(f.filter(val)).isTrue();
        assertThat(f.filter("xyz")).isFalse();
    }
}
