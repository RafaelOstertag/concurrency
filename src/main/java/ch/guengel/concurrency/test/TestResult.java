package ch.guengel.concurrency.test;

import ch.guengel.concurrency.workunits.UnitOfWork;

import java.time.Duration;

public class TestResult {
    private final String workUnit;
    private final String name;
    private final Duration duration;
    private final int repetitions;
    private final int concurrency;

    public TestResult(ConcurrencyTest concurrencyTest, UnitOfWork<?> unitOfWork, Duration duration, int repetitions, int concurrency) {
        this.name = concurrencyTest.getClass().getSimpleName();
        this.workUnit = unitOfWork.getClass().getSimpleName();
        this.duration = duration;
        this.repetitions = repetitions;
        this.concurrency = concurrency;
    }

    @Override
    public String toString() {
        return workUnit + "," +
                name + "," +
                duration.toMillis() + "," +
                repetitions + "," +
                concurrency;
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

    public int getRepetitions() {
        return repetitions;
    }

    public int getConcurrency() {
        return concurrency;
    }
}
