package ru.spbstu.telematics.java;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Main {

    public static void main(String[] args) {
        TrafficLight mainTrafficLight = new TrafficLight();
        List<Car> cars = new ArrayList<>();
        List<Thread> carThreads = new ArrayList<>();
        CountDownLatch startSignal = new CountDownLatch(1); // Барьер для старта всех потоков

        for (int i = 0; i < 10; i++) {
            Car car = Car.createRandomCar(mainTrafficLight, startSignal);
            cars.add(car);
            carThreads.add(car.getThread());
        }

        for (Car car : cars) {
            car.start();
        }

        System.out.println("\nВсе машины готовы. Светофор включается!");
        startSignal.countDown();

        for (Thread thread : carThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                thread.interrupt();
            }
        }

        System.out.println("\nВсе " + mainTrafficLight.getTotalPassedCars() + " машины проехали. Bye-bye! ");
    }
}
