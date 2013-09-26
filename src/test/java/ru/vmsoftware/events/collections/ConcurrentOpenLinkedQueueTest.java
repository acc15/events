package ru.vmsoftware.events.collections;

import org.junit.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-26-09
 */
public class ConcurrentOpenLinkedQueueTest {


    private boolean simpleBoolean = false;

    private AtomicReference<?> hint = new AtomicReference<Object>();

    private Executor executor = Executors.newCachedThreadPool();

    private static boolean waitBarrier(CyclicBarrier barrier) {
        try {
            barrier.await();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class TestQueue<T> implements SimpleQueue<T> {
        public boolean isEmpty() {
            return entryQueue.isEmpty();
        }

        public void clear() {
            entryQueue.clear();
        }

        public void add(T value) {
            entryQueue.add(new ConcurrentOpenLinkedQueue.ConcurrentLinkedEntry<T>(value));
        }

        public Iterator<T> iterator() {
            return new Iterator<T>() {
                public boolean hasNext() {
                    return iter.hasNext();
                }

                public T next() {
                    return iter.next().getValue();
                }

                public void remove() {
                    iter.remove();
                }

                private Iterator<ConcurrentOpenLinkedQueue.ConcurrentLinkedEntry<T>> iter =
                        entryQueue.iterator();
            };
        }

        private ConcurrentOpenLinkedQueue<ConcurrentOpenLinkedQueue.ConcurrentLinkedEntry<T>> entryQueue =
                new ConcurrentOpenLinkedQueue<ConcurrentOpenLinkedQueue.ConcurrentLinkedEntry<T>>();
    }

    @Test(timeout = 1000)
    public void testLazySetSynchoronizesVariable() throws Exception {
        new Thread(new Runnable() {
            public void run() {
                simpleBoolean = true;

                int x = 0;
                for (;;) {
                    x++;
                }
            }
        }).start();

        int x = 0;
        while (!simpleBoolean) {
            ++x;
            hint.lazySet(null);
        }
        System.out.println(x + "###DONE");
    }


    @Test
    public void testConsecutiveRemoval() throws Exception {

        final int runTimes = 100;
        final int sequenceSize = 150;

        for (int r=0;r<runTimes;r++) {
            System.out.println("#####");
            System.out.println("##### Run count: " + r);
            System.out.println("#####");

            final TestQueue<Integer> queue = new TestQueue<Integer>();

            final List<Integer> testList = new ArrayList<Integer>();
            for (int i=0; i<sequenceSize + runTimes; i++) {
                testList.add(i);
                queue.add(i);
            }

            final int startOffset = r;

            final CyclicBarrier barrier = new CyclicBarrier(sequenceSize);
            final CountDownLatch latch = new CountDownLatch(sequenceSize);
            for (int i=0; i<sequenceSize; i++) {
                final int value = i;
                executor.execute(new Runnable() {
                    public void run() {
                        final Iterator<Integer> itr = queue.iterator();
                        final String threadId = "Thread " + Thread.currentThread().getId();
                        for (int i=0; i<=value+startOffset; i++) {
                            final Integer v = itr.next();
                            if (i == value) {
                                System.out.println(threadId + ": value on itr: " + v);
                            }
                        }
                        if (!waitBarrier(barrier)) {
                            return;
                        }
                        itr.remove();
                        System.out.println(threadId + ": remove");
                        latch.countDown();
                    }
                });
            }
            latch.await();

            testList.subList(startOffset, startOffset + sequenceSize).clear();
            assertThat(queue).containsExactly(testList.toArray(new Integer[testList.size()]));
        }
    }

    @Test
    public void testAppendDontMissSomething() throws Exception {

        final int addCount = 150;
        final int threadCount = 150;

        final CyclicBarrier barrier = new CyclicBarrier(threadCount);

        final TestQueue<Integer> queue = new TestQueue<Integer>();
        for (int i=0; i<threadCount; i++) {
            final int t = i;
            executor.execute(new Runnable() {
                public void run() {
                    if (!waitBarrier(barrier)) {
                        return;
                    }
                    for (int i=0; i<addCount; i++) {
                        queue.add(t*addCount+i);
                    }
                }
            });
        }

        final Set<Integer> expectedValues = new HashSet<Integer>();
        for (int i=0; i<threadCount*addCount; i++) {
            expectedValues.add(i);
        }
        assertThat(queue).containsAll(expectedValues);
    }

    @Test
    public void testRemoveLastAppend() throws Exception {

        final int runTimes = 100;

        for (int r=0;r<runTimes;r++) {



        }



    }
}
