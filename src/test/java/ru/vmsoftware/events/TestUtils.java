package ru.vmsoftware.events;

import org.apache.commons.lang.ObjectUtils;
import org.fest.assertions.api.Assertions;

import java.lang.ref.WeakReference;
import java.util.Iterator;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * @author Vyacheslav Mayorov
 * @since 2013-01-05
 */
public class TestUtils {

    public static final Object NULL = new Object();

    public static void forceGC() throws InterruptedException{
        final WeakReference<Object> ref = new WeakReference<Object>(new Object());
        while (ref.get() != null) {
            System.gc();
        }
        Thread.sleep(200);
    }

    public static <L,R> void assertIterator(Iterator<L> iter, R... values) {
        assertIterator(new Equalizer<L, R>() {
            @Override
            public boolean equals(L o1, R o2) {
                return ObjectUtils.equals(o1, o2);
            }
        }, iter, values);
    }

    public static <L,R> void assertIterator(Equalizer<L,R> equalizer, Iterator<L> iter, R... values) {
        for (int i=0;i<values.length;i++) {
            assertThat(iter.hasNext()).as("item ["+i+"] hasNext() fail").isTrue();
            Assertions.assertThat(equalizer.equals(iter.next(), values[i])).as("item ["+i+"] next() fail").isTrue();
        }
        assertThat(iter.hasNext()).as("final hasNext() check").isFalse();
    }
}
