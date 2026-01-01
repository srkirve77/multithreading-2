package com.example.multithreading.l_register_callback;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class RegisterCallback {
    public static void main(String[] args) throws InterruptedException {
        RegisterCallbackImpl registerCallbackImpl = new RegisterCallbackImpl();
        int noOfThreads = 10000;
        List<Thread> threads = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        Random random = new Random();

        for (int i = 0 ; i < noOfThreads ; i++) {
            Long randomLong = random.nextLong(1000);
            final CallackConfig callackConfig = new CallackConfig(currentTime + randomLong, i);
            threads.add(new Thread(() -> {
                //System.out.println("Scheduling callback threadNo. " + callackConfig.threadNo + " callbackTime: "
                //        + LocalDateTime.ofInstant(Instant.ofEpochMilli(callackConfig.callbackTime), ZoneId.systemDefault()));
                registerCallbackImpl.registerCallback(callackConfig);
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

class RegisterCallbackImpl {
    PriorityQueue<CallackConfig> pq;
    CallbackResource callbackResource;
    ReentrantLock lock = new ReentrantLock();

    public RegisterCallbackImpl() {
        pq = new PriorityQueue<>();
        callbackResource = new CallbackResource();
        Thread demonThread1 = new Thread(() -> {
            while (true) {
                long currentTime = System.currentTimeMillis();
                lock.lock();
                if (!pq.isEmpty() && pq.peek().callbackTime <= currentTime) {
                    CallackConfig callackConfig = pq.remove();
                    callbackResource.callback(callackConfig);
                }
                lock.unlock();
            }
        });

        Thread demonThread2 = new Thread(() -> {
            while (true) {
                long currentTime = System.currentTimeMillis();
                lock.lock();
                if (!pq.isEmpty() && pq.peek().callbackTime <= currentTime) {
                    CallackConfig callackConfig = pq.remove();
                    callbackResource.callback(callackConfig);
                }
                lock.unlock();
            }
        });

        demonThread1.start();
        //demonThread2.start();
    }

    public void registerCallback(CallackConfig callackConfig) {
        lock.lock();
        pq.add(callackConfig);
        lock.unlock();
    }
}

class CallackConfig implements Comparable<CallackConfig> {
    long callbackTime;
    int threadNo;

    public CallackConfig(long callbackTime, int threadNo) {
        this.callbackTime = callbackTime;
        this.threadNo = threadNo;
    }

    @Override
    public int compareTo(CallackConfig o) {
        return Long.compare(this.callbackTime, o.callbackTime);
    }
}

class CallbackResource {
    int callbackReceivedOnTime;
    int totalCallbackReceived;

    public void callback(CallackConfig callackConfig) {
        totalCallbackReceived += 1;
        long currentTime = System.currentTimeMillis();
        long toleranceTime = 50;
        if (currentTime > callackConfig.callbackTime + toleranceTime) {
            //System.out.println("callback received at incorrect time. Expected: " + LocalDateTime.ofInstant(Instant.ofEpochMilli(callackConfig.callbackTime), ZoneId.systemDefault()) +
            //"Actual: " + LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTime), ZoneId.systemDefault()));
        } else {
            callbackReceivedOnTime += 1;
            //System.out.println("Received callback for threadNo: " + callackConfig.threadNo + " Time: "
            //        + LocalDateTime.ofInstant(Instant.ofEpochMilli(callackConfig.callbackTime), ZoneId.systemDefault()) + " Total callback received: " + totalCallbackReceived + " Callback Received on Time: " + callbackReceivedOnTime);
            System.out.println("Total callback received: " + totalCallbackReceived + " Callback Received on Time: " + callbackReceivedOnTime);
        }
    }
}