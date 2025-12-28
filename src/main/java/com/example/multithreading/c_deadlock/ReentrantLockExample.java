package com.example.multithreading.c_deadlock;

public class ReentrantLockExample {

    public static void main(String args[]) throws Exception {
        NonReentrantLock nreLock = new NonReentrantLock();

        // First locking would be successful
        nreLock.lock();
        System.out.println("Acquired first lock");        
        // Second locking results in a self deadlock 
        System.out.println("Trying to acquire second lock");      
        nreLock.lock();
        System.out.println("Acquired second lock");
    }
}

class NonReentrantLock {

    boolean isLocked;

    public NonReentrantLock() {
        isLocked = false;
    }

    public void lock() throws InterruptedException {
        while (isLocked) {
            wait();
        }
        isLocked = true;
    } 

    public void unlock() {
        isLocked = false;
        notify();
    }
}