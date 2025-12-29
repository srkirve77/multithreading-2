package com.example.multithreading.d_badsync;

public class A_BadSynchronization {
    public static void main(String args[]) throws InterruptedException {
        doNothing();
    }

    public static void doNothing() throws InterruptedException {
        Object dummyObject = new Object();
        dummyObject.wait();
    }
}

