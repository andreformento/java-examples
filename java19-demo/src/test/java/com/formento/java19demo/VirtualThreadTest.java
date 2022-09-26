package com.formento.java19demo;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirtualThreadTest {

    private class RequestFake implements Runnable {
        private static final Random RANDOM = new Random();
        private final Integer index;
        private final Long milliseconds;

        private RequestFake(Integer index) {
            this.index = index;
            this.milliseconds = RANDOM.nextLong(1000);
        }

        public void run() {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " [" + index + "] Ran in " + milliseconds + "ms");
        }
    }

    @Test
    public void virtualThread() {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10; i++) {
                executor.execute(new RequestFake(i));
            }
//            executor.shutdown();
        }
    }

}
