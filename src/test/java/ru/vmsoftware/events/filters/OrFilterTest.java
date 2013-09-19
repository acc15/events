package ru.vmsoftware.events.filters;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-20-09
 */
public class OrFilterTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testFilter() throws Exception {
        final Filter<String> f = Filters.or(Filters.equalTo("xyz"), Filters.equalTo("abc"));
        assertThat(f.filter("abc")).isTrue();
        assertThat(f.filter("xyz")).isTrue();
        assertThat(f.filter("dac")).isFalse();
    }
}
