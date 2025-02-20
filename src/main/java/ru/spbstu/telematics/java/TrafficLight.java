package ru.spbstu.telematics.java;

import java.util.*;
import java.util.concurrent.*;

public class TrafficLight {

    private final Semaphore semaphore = new Semaphore(2, true);
    private final Map<Direction, Integer> carsQueue = new ConcurrentHashMap<>();
    private final Queue<Direction> waitingQueue = new ConcurrentLinkedQueue<>();
    private final Set<Direction> activeDirections = ConcurrentHashMap.newKeySet();
    private final Object lock = new Object();

    public TrafficLight() {
        for (Direction direction : Direction.values()) {
            carsQueue.put(direction, 0);
        }
    }

    public void incrementQueue(Direction direction) {
        carsQueue.put(direction, carsQueue.get(direction) + 1);
        waitingQueue.offer(direction);
    }

    public void passThroughIntersection(Direction direction) {

        try {
            synchronized (lock) {
                // Ждём, пока направление не пересекается с уже активными
                while (activeDirections.stream().anyMatch(d -> !d.doesNotIntersect(direction))) {
                    lock.wait();
                }
                activeDirections.add(direction);
            }

            semaphore.acquire();
            System.out.println(Thread.currentThread().getName() + " 🚗 Поток захватил доступный слот семафора : " + direction);
            Thread.sleep(5000);
            System.out.println(Thread.currentThread().getName() + " ✅ Машина уехала: " + direction);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            synchronized (lock) {
                activeDirections.remove(direction);
                lock.notifyAll();
            }
            System.out.println(Thread.currentThread().getName() + " Поток освободил слот семафора ");
            semaphore.release();
        }
    }
}