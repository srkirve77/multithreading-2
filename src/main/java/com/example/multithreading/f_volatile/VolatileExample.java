package com.example.multithreading.f_volatile;

class VolatileExample {

    // volatile doesn't imply thread-safety!
    static volatile int count = 0;

    public synchronized static void inc() {
        for (int i = 0; i < 1000; i++)
            count++;
    }

    public static void main(String[] args) throws InterruptedException {

        int numThreads = 100;
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    inc();
                }
            });
        }

        for (int i = 0; i < numThreads; i++) {
            threads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }

        System.out.println("count = " + count);
    }
}