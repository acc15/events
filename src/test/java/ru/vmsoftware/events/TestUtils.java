package ru.vmsoftware.events;

import org.apache.commons.lang.ObjectUtils;
import org.fest.assertions.api.Assertions;
import ru.vmsoftware.events.collections.SimpleIterator;
import ru.vmsoftware.events.collections.SimpleQueue;

import java.lang.ref.WeakReference;
import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * @author Vyacheslav Mayorov
 * @since 2013-01-05
 */
public class TestUtils {

    public static final Object NULL = new Object();

    public static class CallRecorder {

        public void recordCall(Object... args) {
            if (this.args != null) {
                throw new IllegalStateException("only one call can be recorder by CallRecorder");
            }
            this.args = Arrays.asList(args);
        }

        public List<Object> getCallArgs() {
            return args;
        }

        public boolean isCalled() {
            return args != null;
        }

        private List<Object> args;
    }

    public static <T> List<T> tail(List<T> l, int count) {
        if (l.size() > count) {
            return l.subList(l.size()-count, l.size());
        } else {
            return l;
        }
    }

    public static void forceGC() throws InterruptedException {
        final WeakReference<Object> ref = new WeakReference<Object>(new Object());
        while (ref.get() != null) {
            System.gc();
        }
        Thread.sleep(200);
    }

    public static <T> Iterable<T> makeIterable(final SimpleQueue<T> simpleQueue) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return makeIterator(simpleQueue.iterator());
            }
        };
    }

    public static <T> Iterator<T> makeIterator(final SimpleIterator<T> iter) {
        return new Iterator<T>() {

            private T next = iter.next();

            public boolean hasNext() {
                return next != null;
            }

            public T next() {
                if (this.next == null) {
                    throw new IllegalStateException();
                }
                final T result = this.next;
                this.next = iter.next();
                return result;
            }

            public void remove() {
                iter.remove();
            }
        };
    }

    public static <L, R> void assertIterator(Iterator<L> iter, R... values) {
        assertIterator(new Equalizer<L, R>() {
            public boolean equals(L o1, R o2) {
                return ObjectUtils.equals(o1, o2);
            }
        }, iter, values);
    }

    public static <L, R> void assertIterator(Equalizer<L, R> equalizer, Iterator<L> iter, R... values) {
        for (int i = 0; i < values.length; i++) {
            assertThat(iter.hasNext()).as("item [" + i + "] hasNext() fail").isTrue();
            Assertions.assertThat(equalizer.equals(iter.next(), values[i])).as("item [" + i + "] next() fail").isTrue();
        }
        assertThat(iter.hasNext()).as("final hasNext() check").isFalse();
    }
}
