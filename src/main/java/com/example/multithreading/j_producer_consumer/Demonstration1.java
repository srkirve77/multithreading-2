package com.example.multithreading.j_producer_consumer;

public class Demonstration1 {
    public static void main(String args[]) throws Exception {
        final BlockingQueue<Integer> q = new BlockingQueue<Integer>(5);

        Thread t1 = new Thread(() -> {
            while (true) {
                for (int i = 0; i < 50; i++) {
                    try {
                        q.enqueue(i);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("enqueued : " + i);
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                while (true) {
                    int i = q.dequeue();
                    System.out.println(" dequeued : " + i);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t3 = new Thread(() -> {
            try {
                while (true) {
                    int i = q.dequeue();
                    System.out.println(" dequeued : " + i);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        Thread.sleep(1000);
        t2.join();
        t3.join();
    }
}

class BlockingQueue<T> {
    T[] array;
    Object lock = new Object();
    int size = 0;
    int capacity;
    int head = 0;
    int tail = 0;

    @SuppressWarnings("unchecked")
    public BlockingQueue(int capacity) {
        array = (T[]) new Object[capacity];
        this.capacity = capacity;
    }

    public void enqueue(T item) throws InterruptedException {
        synchronized (lock) {
            while (size == capacity) {
                lock.wait();
            }
            if (tail == capacity) {
                tail = 0;
            }
            array[tail] = item;
            size++;
            tail++;
            lock.notifyAll();
        }
    }

    public T dequeue() throws InterruptedException {
        T item = null;
        synchronized (lock) {
            while (size==0) {
                lock.wait();
            }
            if (head == capacity) {
                head = 0;
            }
            item = array[head];
            array[head] = null;
            head++;
            size--;

            lock.notifyAll();
        }
        return item;
    }
}