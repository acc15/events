package ru.vmsoftware.events.collections;

import org.junit.Test;
import ru.vmsoftware.events.TestUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-26-09
 */
public class ConcurrentOpenLinkedQueueTest {

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

    @Test
    public void testParallelRemoval() throws Exception {

        final int runTimes = 100;
        final int sequenceSize = 150;

        for (int r=0;r<runTimes;r++) {
            final TestConcurrentQueue<Integer> queue = new TestConcurrentQueue<Integer>();

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
                new Thread(new Runnable() {
                    public void run() {
                        final SimpleIterator<Integer> itr = queue.iterator();
                        for (int i=0; i<=value+startOffset; i++) {
                            final Integer v = itr.next();
                            assertThat(v).isEqualTo(i);
                        }
                        if (!waitBarrier(barrier)) {
                            return;
                        }
                        itr.remove();
                        latch.countDown();
                    }
                }).start();
            }
            latch.await();

            testList.subList(startOffset, startOffset + sequenceSize).clear();
            assertThat(TestUtils.makeIterable(queue)).
                    containsExactly(testList.toArray(new Integer[testList.size()]));

            final TestConcurrentQueue.ListReport report = TestConcurrentQueue.reportDeadNodes(queue.getHead());
            System.out.println(report);
            assertThat(report.totalNextNodes).as("totalNextNodes").isEqualTo(testList.size());
            assertThat(report.totalPrevNodes).as("totalPrevNodes").isEqualTo(testList.size());
            assertThat(report.totalNextMarkers).as("totalNextMarkers").isEqualTo(0);
            assertThat(report.totalPrevMarkers).as("totalPrevMarkers").isEqualTo(0);
            assertThat(report.totalPrevIncorrectLinks).as("totalPrevIncorrectLinks").isEqualTo(0);
        }
    }

    @Test
    public void testAddDontMissSomething() throws Exception {

        final int addCount = 50;
        final int threadCount = 150;

        final CyclicBarrier barrier = new CyclicBarrier(threadCount);
        final CountDownLatch latch = new CountDownLatch(threadCount);

        final TestConcurrentQueue<Integer> queue = new TestConcurrentQueue<Integer>();
        for (int i=0; i<threadCount; i++) {
            final int t = i;
            new Thread(new Runnable() {
                public void run() {
                    if (!waitBarrier(barrier)) {
                        return;
                    }
                    for (int i=0; i<addCount; i++) {
                        queue.add(t*addCount+i);
                    }
                    latch.countDown();
                }
            }).start();
        }

        latch.await();

        final Set<Integer> expectedValues = new HashSet<Integer>();
        for (int i=0; i<threadCount*addCount; i++) {
            expectedValues.add(i);
        }

        assertThat(TestUtils.makeIterable(queue)).containsAll(expectedValues);
        System.out.println("FINISH: " + new Date());

    }

//    @Test @Ignore
//    public void testOutOfMemory() throws Exception {
//        final TestConcurrentQueue<Integer> q = new TestConcurrentQueue<Integer>();
//        final Runtime runtime = Runtime.getRuntime();
//        final long wasMemory = runtime.freeMemory();
//        for (int i=0; i<100; i++) {
//            for (int j=0; j<10000; j++) {
//                q.add(j);
//            }
//            q.clear();
//        }
//        final long remainsMemory = runtime.freeMemory();
//        assertThat(wasMemory - remainsMemory).isLessThan(1000000);
//    }

    @Test
    public void testAddAndRemove() throws Exception {

        final int runTimes = 100;
        final int threadCount = 50;

        for (int r=0;r<runTimes;r++) {

            System.out.println("RUN TIME:" + r);

            final CyclicBarrier barrier = new CyclicBarrier(threadCount*2);
            final CountDownLatch latch = new CountDownLatch(threadCount*2);

            final AtomicInteger appendCount = new AtomicInteger();
            final AtomicInteger appendStarted = new AtomicInteger();
            final AtomicInteger removeStarted = new AtomicInteger();
            final AtomicInteger removeCount = new AtomicInteger();
            final TestConcurrentQueue<Integer> queue = new TestConcurrentQueue<Integer>();

            for (int i=0;i<threadCount;i++) {
                new Thread(new Runnable() {
                    public void run() {
                        waitBarrier(barrier);
                        removeStarted.incrementAndGet();
                        for (;;) {
                            final SimpleIterator<Integer> iter = queue.iterator();
                            if (iter.next() != null && iter.remove()) {
                                break;
                            }
                            Thread.yield(); // Note that 'yield'. Without this thread will eat resources without giving other threads to proceed
                            // (which may be an appending thread)
                        }
                        removeCount.incrementAndGet();
                        latch.countDown();
                    }
                }).start();
            }
            for (int i=0;i<threadCount;i++) {
                new Thread(new Runnable() {
                    public void run() {
                        waitBarrier(barrier);
                        appendStarted.incrementAndGet();
                        queue.add(100);
                        appendCount.incrementAndGet();
                        latch.countDown();
                    }
                }).start();
            }
            latch.await(10, TimeUnit.SECONDS);

            System.out.println("Append started: " + appendStarted + "; Remove started: " + removeStarted +
                    "; Append finished: " + appendCount + "; Remove finished: " + removeCount);

            assertThat(appendStarted.get()).as("appendStarted").isEqualTo(threadCount);
            assertThat(removeStarted.get()).as("removeStarted").isEqualTo(threadCount);
            assertThat(appendCount.get()).as("appendCount").isEqualTo(threadCount);
            assertThat(removeCount.get()).as("removeCount").isEqualTo(threadCount);
            assertThat(queue.isEmpty()).isTrue();
        }
    }
}
