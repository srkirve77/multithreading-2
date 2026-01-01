package com.example.multithreading.l_register_callback;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RegisterCallbackV2 {
    public static void main(String[] args) throws InterruptedException {
        RegisterCallbackImplV2 registerCallbackImplV2 = new RegisterCallbackImplV2();
        int noOfThreads = 10000;
        List<Thread> threads = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        Random random = new Random();

        for (int i = 0 ; i < noOfThreads ; i++) {
            Long randomLong = 1000 + random.nextLong(1000);
            final CallackConfigV2 callackConfig = new CallackConfigV2(currentTime + randomLong, i);
            threads.add(new Thread(() -> {
                //System.out.println("Scheduling callback threadNo. " + callackConfig.threadNo + " callbackTime: "
                //        + LocalDateTime.ofInstant(Instant.ofEpochMilli(callackConfig.callbackTime), ZoneId.systemDefault()));
                registerCallbackImplV2.registerCallback(callackConfig);
            }));
        }
        for (int i = 0 ; i < noOfThreads ; i++) {
            threads.get(i).start();
        }
        for (int i = 0 ; i < noOfThreads ; i++) {
            threads.get(i).join();
        }
    }
}

class RegisterCallbackImplV2 {
    PriorityQueue<CallackConfigV2> pq1;
    PriorityQueue<CallackConfigV2> pq2;
    PriorityQueue<CallackConfigV2> pq3;
    CallbackResourceV2 callbackResourceV2;
    ReentrantLock lock1 = new ReentrantLock();
    Condition condition1 = lock1.newCondition();

    ReentrantLock lock2 = new ReentrantLock();
    Condition condition2 = lock2.newCondition();

    ReentrantLock lock3 = new ReentrantLock();
    Condition condition3 = lock3.newCondition();

    public RegisterCallbackImplV2() {
        pq1 = new PriorityQueue<>(4000);
        pq2 = new PriorityQueue<>(4000);
        pq3 = new PriorityQueue<>(4000);
        callbackResourceV2 = new CallbackResourceV2();
        Thread demonThread1 = new Thread(() -> {
            try {
                while (true) {
                    lock1.lock();
                    long currentTime = System.currentTimeMillis();
                    while (pq1.isEmpty()) {
                        condition1.await();
                    }
                    while (!pq1.isEmpty() && pq1.peek().callbackTime <= currentTime) {
                        CallackConfigV2 callackConfig = pq1.remove();
                        callbackResourceV2.callback(callackConfig);
                        currentTime = System.currentTimeMillis();
                    }
                    lock1.unlock();
                    Thread.sleep(1);
                }
            } catch (InterruptedException ie) {

            }
        });

        Thread demonThread2 = new Thread(() -> {
            try {
                while (true) {
                    lock2.lock();
                    long currentTime = System.currentTimeMillis();
                    while (pq2.isEmpty()) {
                        condition2.await();
                    }
                    while (!pq2.isEmpty() && pq2.peek().callbackTime <= currentTime) {
                        CallackConfigV2 callackConfig = pq2.remove();
                        callbackResourceV2.callback(callackConfig);
                        currentTime = System.currentTimeMillis();
                    }
                    lock2.unlock();
                    Thread.sleep(1);
                }
            } catch (InterruptedException ie) {

            }
        });

        Thread demonThread3 = new Thread(() -> {
            try {
                while (true) {
                    lock3.lock();
                    long currentTime = System.currentTimeMillis();
                    while (pq3.isEmpty()) {
                        condition3.await();
                    }
                    while (!pq3.isEmpty() && pq3.peek().callbackTime <= currentTime) {
                        CallackConfigV2 callackConfig = pq3.remove();
                        callbackResourceV2.callback(callackConfig);
                        currentTime = System.currentTimeMillis();
                    }
                    lock3.unlock();
                    Thread.sleep(1);
                }
            } catch (InterruptedException ie) {

            }
        });

        demonThread1.start();
        demonThread2.start();
        demonThread3.start();
    }

    public static long sleepFor(long currentTime, long callbackTime) {
        long additionalLiveliness = 10;
        if (currentTime > callbackTime + additionalLiveliness) {
            return currentTime - callbackTime - additionalLiveliness;
        }
        return 1;
    }

    public void registerCallback(CallackConfigV2 callackConfig) {
        if (callackConfig.threadNo % 3 == 0) {
            lock1.lock();
            pq1.add(callackConfig);
            condition1.signal();
            lock1.unlock();
        } else if (callackConfig.threadNo % 3 == 1) {
            lock2.lock();
            pq2.add(callackConfig);
            condition2.signal();
            lock2.unlock();
        } else if (callackConfig.threadNo % 3 == 2) {
            lock3.lock();
            pq3.add(callackConfig);
            condition3.signal();
            lock3.unlock();
        }
    }
}

class CallackConfigV2 implements Comparable<CallackConfigV2> {
    long callbackTime;
    int threadNo;

    public CallackConfigV2(long callbackTime, int threadNo) {
        this.callbackTime = callbackTime;
        this.threadNo = threadNo;
    }

    @Override
    public int compareTo(CallackConfigV2 o) {
        return Long.compare(this.callbackTime, o.callbackTime);
    }
}

class CallbackResourceV2 {
    AtomicInteger callbackReceivedOnTime = new AtomicInteger(0);
    AtomicInteger totalCallbackReceived = new AtomicInteger(0);
    ReentrantLock lock = new ReentrantLock();

    public void callback(CallackConfigV2 callackConfig) {
        totalCallbackReceived.incrementAndGet();
        long currentTime = System.currentTimeMillis();
        long toleranceTime = 50;
        if (currentTime > callackConfig.callbackTime + toleranceTime) {
            //System.out.println("callback received at incorrect time. Expected: " + LocalDateTime.ofInstant(Instant.ofEpochMilli(callackConfig.callbackTime), ZoneId.systemDefault()) +
            //"Actual: " + LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTime), ZoneId.systemDefault()));
        } else {
            callbackReceivedOnTime.incrementAndGet();
            //System.out.println("Received callback for threadNo: " + callackConfig.threadNo + " Time: "
            //        + LocalDateTime.ofInstant(Instant.ofEpochMilli(callackConfig.callbackTime), ZoneId.systemDefault()) + " Total callback received: " + totalCallbackReceived + " Callback Received on Time: " + callbackReceivedOnTime);
            System.out.println("Total callback received: " + totalCallbackReceived + " Callback Received on Time: " + callbackReceivedOnTime);
        }
    }
}