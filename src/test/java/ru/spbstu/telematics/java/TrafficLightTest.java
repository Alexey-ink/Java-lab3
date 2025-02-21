package ru.spbstu.telematics.java;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class TrafficLightTest {

    @Test
    void testNoDeadlock() throws InterruptedException {
        TrafficLight trafficLight = new TrafficLight();
        List<Car> cars = new ArrayList<>();
        List<Thread> carThreads = new ArrayList<>();
        CountDownLatch startSignal = new CountDownLatch(1); // Барьер старта

        int numCars = 50;

        for (int i = 0; i < numCars; i++) {
            Car car = Car.createRandomCar(trafficLight, startSignal);
            cars.add(car);
            carThreads.add(car.getThread());
        }

        for (Car car : cars) {
            car.start();
        }

        startSignal.countDown();

        for (Thread thread : carThreads) {
            thread.join(20000);
        }

        for (Thread thread : carThreads) {
            assertFalse(thread.isAlive(), "Обнаружен Deadlock!!! Поток завис.");
        }
    }

    @Test
    void testRaceCondition() throws InterruptedException {
        TrafficLight trafficLight = new TrafficLight();
        CountDownLatch startSignal = new CountDownLatch(1);

        int numCars = 100;
        List<Car> cars = new ArrayList<>();
        List<Thread> carThreads = new ArrayList<>();

        for (int i = 0; i < numCars; i++) {
            Car car = Car.createRandomCar(trafficLight, startSignal);
            cars.add(car);
            carThreads.add(car.getThread());
        }

        for (Car car : cars) {
            car.start();
        }

        startSignal.countDown();

        for (Thread thread : carThreads) {
            thread.join(20000);
        }

        int carsPassed = trafficLight.getTotalPassedCars();
        assertEquals(numCars, carsPassed, "Race Condition!!! Ожидалось: " + numCars + ", но проехало: " + carsPassed);
    }
}
