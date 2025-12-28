package com.example.multithreading.basics;

public class TimePassWork extends Thread {
    
    @Override
    public void run() {
        System.out.println("Thread is running");
    }
}