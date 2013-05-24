package ru.vmsoftware.events.filters;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-28-04
 */
public class StringFilterTest {

    enum TestEnum {
        A,
        B
    }

    enum TestEnum2 {
        A
    }

    @Test
    public void testFilterReturnTrueOnlyForSpecificValues() throws Exception {
        final Filter<Object> filter = new StringFilter<Object>("A");
        assertThat(filter.filter(TestEnum.A)).isTrue();
        assertThat(filter.filter(TestEnum2.A)).isTrue();
        assertThat(filter.filter(TestEnum.B)).isFalse();
    }


}
