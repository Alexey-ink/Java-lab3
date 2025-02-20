package ru.spbstu.telematics.java;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Car implements Runnable{

    final Direction direction;
    private final TrafficLight trafficLight;
    private final Thread thread;
    private final CountDownLatch startSignal; // Барьер ожидания

    public Car(Direction direction, TrafficLight trafficLight, CountDownLatch startSignal) {
        this.direction = direction;
        this.trafficLight = trafficLight;
        this.startSignal = startSignal;
        this.thread = new Thread(this);

        synchronized (trafficLight) {
            trafficLight.incrementQueue(direction);
        }
    }

    public static Car createRandomCar(TrafficLight trafficLight, CountDownLatch startSignal) {
        Random random = new Random();
        Direction direction = Direction.values()[random.nextInt(Direction.values().length)];
        return new Car(direction, trafficLight, startSignal);
    }

    @Override
    public void run() {
        try {
            startSignal.await(); // Ждём команды на старт
            System.out.println(Thread.currentThread().getName() + " 🚗 Подъехала машина, направление: " + direction);
            trafficLight.passThroughIntersection(direction);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Thread getThread() {
        return thread;
    }

    public void start() {
        thread.start();
    }
}
