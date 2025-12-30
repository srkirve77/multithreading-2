package com.example.multithreading.e_interrupt;

public class InterruptExample {
    public static void main(String args[]) throws InterruptedException {
        InterruptExampleImpl.example();
    }
}

class InterruptExampleImpl {

    static public void example() throws InterruptedException {
        final Thread sleepyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("I am too sleepy... Let me sleep for an hour.");
                    Thread.sleep(1000*60*60);
                } catch (InterruptedException ie) {
                    System.out.println("the interrupted flag is cleared : " + Thread.interrupted() + " " +
                            Thread.currentThread().isInterrupted());
                    Thread.currentThread().interrupt();
                    System.out.println("Someone woke me up!");
                    System.out.println("The interrupted flag is set now " + Thread.currentThread().isInterrupted() + " " + Thread.interrupted());
                }
            }
        });

        sleepyThread.start();
        Thread.sleep(2000);
        System.out.println("About to wake up the sleepy thread ...");
        sleepyThread.interrupt();
        System.out.println("Woke up sleepy thread ...");
        sleepyThread.join();
    }

}
