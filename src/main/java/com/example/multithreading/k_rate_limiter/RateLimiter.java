package com.example.multithreading.k_rate_limiter;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RateLimiter {
    public static void main(String[] args) throws InterruptedException {
        RateLimiterImpl rateLimiterImpl = new RateLimiterImpl(4);

        Thread t1 = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Requesting token for t1");
                    if (rateLimiterImpl.getToken()) {
                        System.out.println("Received token for t1");
                    } else {
                        System.out.println("Failed to get token for t1");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Requesting token for t2");
                    if (rateLimiterImpl.getToken()) {
                        System.out.println("Received token for t2");
                    } else {
                        System.out.println("Failed to get token for t2");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread t3 = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Requesting token for t3");
                    if (rateLimiterImpl.getToken()) {
                        System.out.println("Received token for t3");
                    } else {
                        System.out.println("Failed to get token for t3");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread t4 = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Requesting token for t4");
                    if (rateLimiterImpl.getToken()) {
                        System.out.println("Received token for t4");
                    } else {
                        System.out.println("Failed to get token for t4");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread t5 = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Requesting token for t5");
                    if (rateLimiterImpl.getToken()) {
                        System.out.println("Received token for t5");
                    } else {
                        System.out.println("Failed to get token for t5");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread t6 = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Requesting token for t6");
                    if (rateLimiterImpl.getToken()) {
                        System.out.println("Received token for t6");
                    } else {
                        System.out.println("Failed to get token for t6");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread t7 = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Requesting token for t7");
                    if (rateLimiterImpl.getToken()) {
                        System.out.println("Received token for t7");
                    } else {
                        System.out.println("Failed to get token for t7");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t7.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();
        t5.join();
        t6.join();
        t7.join();

    }
}

class RateLimiterImpl {
    public static int maxTokensAllowedPerSecond = 10;
    public static long lastTokenRefreshedTime;
    public static AtomicInteger tokensAvailable = new AtomicInteger();
    public Semaphore semaphore = new Semaphore(1, true);
    public ReentrantLock lock = new ReentrantLock();
    public Condition condition = lock.newCondition();

    public RateLimiterImpl(int maxTokensPerSecond) {
        lastTokenRefreshedTime = System.currentTimeMillis();
        maxTokensAllowedPerSecond = maxTokensPerSecond;
        tokensAvailable.getAndSet(maxTokensAllowedPerSecond);
        Thread demoThread = new Thread(this::demonThread);
        demoThread.setDaemon(true);
        demoThread.start();
    }

    private void demonThread() {
        while (true) {
            long currentTokenRefreshTime = System.currentTimeMillis();
            if (currentTokenRefreshTime - lastTokenRefreshedTime > 1000) {
                lock.lock();
                tokensAvailable.getAndSet(maxTokensAllowedPerSecond);
                lastTokenRefreshedTime = currentTokenRefreshTime;
                condition.signalAll();
                lock.unlock();
            }
        }
    }


    public boolean getToken() throws InterruptedException {
        lock.lock();
        while (tokensAvailable.get() == 0) {
            condition.await();
        }
        if (tokensAvailable.get() > 0) {
            tokensAvailable.decrementAndGet();
            lock.unlock();
            return true;
        }
        lock.unlock();
        return false;
    }
}
