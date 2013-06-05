package ru.vmsoftware.events.collections.concurrent;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-05-06
 */
public class ConcurrentLinkedOpenQueueTest {

    private static final int THREAD_COUNT = 100;

    private static final Logger log = Logger.getLogger(ConcurrentLinkedOpenQueueTest.class.getName());

    private ConcurrentLinkedOpenQueue<Integer, ConcurrentLinkedEntryImpl<Integer>> q =
            new ConcurrentLinkedOpenQueue<Integer, ConcurrentLinkedEntryImpl<Integer>>();

    private void testAdd() throws Exception {

        q.clear();

        final CountDownLatch cdIn = new CountDownLatch(THREAD_COUNT);
        final CountDownLatch cdOut = new CountDownLatch(THREAD_COUNT);

        for (int i=0; i<THREAD_COUNT; i++) {
            final int number = i;
            new Thread(new Runnable() {
                public void run() {

                    log.info("thread " + number + " init");

                    cdIn.countDown();
                    try {
                        cdIn.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    log.info("thread " + number + " proceed");
                    q.add(new ConcurrentLinkedEntryImpl<Integer>(number));

                    log.info("entry added from " + number + " thread");
                    cdOut.countDown();
                }
            }).start();
        }

        cdOut.await();

        log.info("all threads was finished. Checking for result...");

        final List<Integer> actualValues = new ArrayList<Integer>();
        for (ConcurrentLinkedEntryImpl<Integer> e = q.first(); e != null; e = e.getNext()) {
            actualValues.add(e.getValue());
        }
        assertThat(actualValues).hasSize(THREAD_COUNT);

        final Integer[] expectedValues = new Integer[THREAD_COUNT];
        for (int i=0; i<THREAD_COUNT; i++) {
            expectedValues[i] = i;
        }

        assertThat(actualValues).containsOnly(expectedValues);

        for (ConcurrentLinkedEntryImpl<Integer> e = q.last().getNext(); e != null; e = e.getNext()) {
            log.warning("last points to non-last entry");
        }
    }

    @Test
    public void testConcurrentAdd() throws Exception {
        testAdd();
    }
}
