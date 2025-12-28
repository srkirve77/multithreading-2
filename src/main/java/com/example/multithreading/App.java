package com.example.multithreading;

import com.example.multithreading.a_basics.TimePassWork;
import com.example.multithreading.a_basics.SumUpExample;

public class App {
    public static void main(String[] args) throws InterruptedException {
        /**
        Running TimePassWork 
        Thread thread = new Thread();
        TimePassWork timePassWork = new TimePassWork();
        Thread t = new Thread(timePassWork);
        t.start();        
        */ 

        SumUpExample.runTest();
    }
}
