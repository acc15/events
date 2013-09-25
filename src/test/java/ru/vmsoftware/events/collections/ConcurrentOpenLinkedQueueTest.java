package ru.vmsoftware.events.collections;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-26-09
 */
public class ConcurrentOpenLinkedQueueTest {


    private boolean simpleBoolean = false;

    private AtomicReference<?> hint = new AtomicReference<Object>();

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
}
