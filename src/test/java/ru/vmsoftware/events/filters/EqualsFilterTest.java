package ru.vmsoftware.events.filters;

import org.junit.Test;


import static org.fest.assertions.api.Assertions.assertThat;


/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
public class EqualsFilterTest {
    @Test
    public void testFilter() throws Exception {
        final String filterValue = "abc";
        final Filter<String> eqFilter = new EqualsFilter<String>(filterValue);
        assertThat(eqFilter.filter(filterValue)).isTrue();
        assertThat(eqFilter.filter("abcd")).isFalse();
    }
}
