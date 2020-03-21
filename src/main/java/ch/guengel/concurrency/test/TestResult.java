package ch.guengel.concurrency.test;

import ch.guengel.concurrency.workunits.UnitOfWork;

import java.time.Duration;

public class TestResult {
    private final String workUnit;
    private final String name;
    private final Duration duration;
    private final int numberOfWorkUnits;
    private final int expectedConcurrency;

    public TestResult(ConcurrencyTest concurrencyTest, UnitOfWork<?> unitOfWork, Duration duration, int numberOfWorkUnits, int expectedConcurrency) {
        this.name = concurrencyTest.getClass().getSimpleName();
        this.workUnit = unitOfWork.getClass().getSimpleName();
        this.duration = duration;
        this.numberOfWorkUnits = numberOfWorkUnits;
        this.expectedConcurrency = expectedConcurrency;
    }

    @Override
    public String toString() {
        return workUnit + "," +
                name + "," +
                duration.toMillis() + "," +
                numberOfWorkUnits + "," +
                expectedConcurrency;
    }

    public String getWorkUnit() {
        return workUnit;
    }

    public String getName() {
        return name;
    }

    public Duration getDuration() {
        return duration;
    }

    public int getNumberOfWorkUnits() {
        return numberOfWorkUnits;
    }

    public int getExpectedConcurrency() {
        return expectedConcurrency;
    }
}
