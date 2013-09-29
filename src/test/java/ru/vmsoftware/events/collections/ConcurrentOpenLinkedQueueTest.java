package ru.vmsoftware.events.collections;

import org.junit.Test;
import ru.vmsoftware.events.TestUtils;

import java.util.*;
import java.util.concurrent.*;

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
    public void testConsecutiveRemoval() throws Exception {

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
    public void testAppendDontMissSomething() throws Exception {

        final int addCount = 150;
        final int threadCount = 150;

        final CyclicBarrier barrier = new CyclicBarrier(threadCount);

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
                }
            }).start();
        }

        final Set<Integer> expectedValues = new HashSet<Integer>();
        for (int i=0; i<threadCount*addCount; i++) {
            expectedValues.add(i);
        }


        assertThat(TestUtils.makeIterable(queue)).containsAll(expectedValues);
        System.out.println("FINISH: " + new Date());

    }

    @Test
    public void testRemoveLastAppend() throws Exception {

        final int runTimes = 100;

        for (int r=0;r<runTimes;r++) {



        }



    }
}
