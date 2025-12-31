package com.example.multithreading.j_producer_consumer;

import java.util.concurrent.Semaphore;

public class Demonstration3 {
    public static void main(String[] args) throws InterruptedException {
        final BlockingQueueWithSemaphore<Integer> q = new BlockingQueueWithSemaphore<>(5);

        Thread producer1 = new Thread(() -> {
            try {
                int i = 1;
                while (true) {
                    q.enqueue(i);
                    System.out.println("Producer thread 1 enqueued " + i);
                    Thread.sleep(1000);
                    i++;
                }
            } catch (InterruptedException ignored) {
            }
        });

        Thread producer2 = new Thread(() -> {
            try {
                int i = 5000;
                while (true) {
                    q.enqueue(i);
                    System.out.println("Producer thread 2 enqueued " + i);
                    Thread.sleep(1000);
                    i++;
                }
            } catch (InterruptedException ie) {

            }
        });

        Thread consumer1 = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(400);
                    System.out.println("Consumer thread 1 dequeued " + q.dequeue());
                }
            } catch (InterruptedException ie) {

            }
        });

        Thread consumer2 = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(400);
                    System.out.println("Consumer thread 2 dequeued " + q.dequeue());
                }
            } catch (InterruptedException ie) {

            }
        });

        Thread consumer3 = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(400);
                    System.out.println("Consumer thread 3 dequeued " + q.dequeue());
                }
            } catch (InterruptedException ie) {

            }
        });

//        producer1.setDaemon(true);
//        producer2.setDaemon(true);
//        producer3.setDaemon(true);
//        consumer1.setDaemon(true);
//        consumer2.setDaemon(true);
//        consumer3.setDaemon(true);

        producer1.start();
        producer2.start();
        //producer3.start();

        consumer1.start();
        consumer2.start();
        consumer3.start();

        Thread.sleep(1000);

        producer1.join();
        producer2.join();
        //producer3.join();

        consumer1.join();
        consumer2.join();
        consumer3.join();
    }
}
class BlockingQueueWithSemaphore<T> {
    T[] array;
    int size = 0;
    int capacity;
    int head = 0;
    int tail = 0;
    Semaphore semLock = new Semaphore(1, false);
    Semaphore semProducer;
    Semaphore semConsumer;

    @SuppressWarnings("unchecked")
    public BlockingQueueWithSemaphore(int capacity) {
        // The casting results in a warning
        array = (T[]) new Object[capacity];
        this.capacity = capacity;
        this.semProducer = new Semaphore(capacity, false);
        this.semConsumer = new Semaphore(0, false);
    }

    public T dequeue() throws InterruptedException {

        T item = null;

        semConsumer.acquire();
        semLock.acquire();

        if (head == capacity) {
            head = 0;
        }

        item = array[head];
        array[head] = null;
        head++;
        size--;

        semLock.release();
        semProducer.release();

        return item;
    }

    public void enqueue(T item) throws InterruptedException {

        semProducer.acquire();
        semLock.acquire();

        if (tail == capacity) {
            tail = 0;
        }

        array[tail] = item;
        size++;
        tail++;

        System.out.print("Queue state: ");
        for (int i = 0 ; i < tail ; i ++) {
            System.out.print(array[i] + " -> ");
        }
        System.out.println();

        semLock.release();
        semConsumer.release();
    }
}