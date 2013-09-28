package ru.vmsoftware.events.collections;

import org.apache.commons.lang.ObjectUtils;
import org.junit.Test;
import ru.vmsoftware.events.TestUtils;

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

        public boolean remove(T value) {
            ConcurrentOpenLinkedQueue.ConcurrentLinkedEntry<T> entry;
            final SimpleIterator<ConcurrentOpenLinkedQueue.ConcurrentLinkedEntry<T>> iter = entryQueue.iterator();
            while ((entry = iter.next()) != null) {
                if (ObjectUtils.equals(value, entry.getValue())) {
                    iter.remove();
                    return true;
                }
            }
            return false;
        }

        public SimpleIterator<T> iterator() {
            return new SimpleIterator<T>() {
                public T next() {
                    final ConcurrentOpenLinkedQueue.ConcurrentLinkedEntry<T> entry = iter.next();
                    if (entry == null) {
                        return null;
                    }
                    return entry.getValue();
                }

                public boolean remove() {
                    return iter.remove();
                }

                private SimpleIterator<ConcurrentOpenLinkedQueue.ConcurrentLinkedEntry<T>> iter =
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
                        final SimpleIterator<Integer> itr = queue.iterator();
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
            TestUtils.assertIterator(
                    TestUtils.makeIterator(queue.iterator()),
                    testList.toArray(new Integer[testList.size()]));
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
        assertThat(TestUtils.makeIterable(queue)).containsAll(expectedValues);
    }

    @Test
    public void testRemoveLastAppend() throws Exception {

        final int runTimes = 100;

        for (int r=0;r<runTimes;r++) {



        }



    }
}
