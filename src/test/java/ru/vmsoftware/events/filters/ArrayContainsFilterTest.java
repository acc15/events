package ru.vmsoftware.events.filters;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
public class ArrayContainsFilterTest {
    @Test
    public void testFilter() throws Exception {

        final ArrayContainsFilter<String> filter = new ArrayContainsFilter<String>("a", "b", "c");
        assertThat(filter.filter("a")).isTrue();
        assertThat(filter.filter("b")).isTrue();
        assertThat(filter.filter("c")).isTrue();
        assertThat(filter.filter("d")).isFalse();
        assertThat(filter.filter("90")).isFalse();


    }
}
