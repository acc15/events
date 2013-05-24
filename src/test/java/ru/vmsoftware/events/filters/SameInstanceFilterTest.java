package ru.vmsoftware.events.filters;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * @author Vyacheslav Mayorov
 * @since 2013-03-05
 */
public class SameInstanceFilterTest {
    @Test
    public void testFilter() throws Exception {

        final Integer a = 123;
        final SameInstanceFilter<Integer> filter = new SameInstanceFilter<Integer>(a);
        assertThat(filter.filter(a)).isTrue();
        assertThat(filter.filter(123)).isTrue();
        assertThat(filter.filter(1)).isFalse();

    }
}
