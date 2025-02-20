package ru.spbstu.telematics.java;

public enum Direction {

    North_South,
    South_North,
    West_East,
    East_West,
    East_South;

    public boolean doesNotIntersect(Direction other) {
        return switch (this) {
            case North_South -> (other == South_North || other == East_South);
            case South_North -> (other == North_South);
            case West_East -> (other == East_West);
            case East_West -> (other == West_East || other == East_South);
            case East_South -> (other == North_South || other == East_West);
            default -> throw new IllegalStateException("Unexpected value: " + this);
        };
    }
}
