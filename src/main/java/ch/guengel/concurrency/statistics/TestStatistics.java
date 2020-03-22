package ch.guengel.concurrency.statistics;

public class TestStatistics {
    private final String workUnit;
    private final String name;
    private final int numberOfWorkUnits;
    private final int expectedConcurrency;
    private final int numberOfSamples;
    private final double mean;
    private final double min;
    private final double max;
    private final double stdDev;

    public TestStatistics(String workUnit, String name, int numberOfWorkUnits, int expectedConcurrency, int numberOfSamples, double mean, double min, double max, double stdDev) {
        this.workUnit = workUnit;
        this.name = name;
        this.numberOfWorkUnits = numberOfWorkUnits;
        this.expectedConcurrency = expectedConcurrency;
        this.numberOfSamples = numberOfSamples;
        this.mean = mean;
        this.min = min;
        this.max = max;
        this.stdDev = stdDev;
    }

    @Override
    public String toString() {
        return workUnit + ',' +
                name + ',' +
                numberOfWorkUnits + ',' +
                expectedConcurrency + ',' +
                numberOfSamples + ',' +
                mean + ',' +
                min + ',' +
                max + ',' +
                stdDev;
    }

    public String getWorkUnit() {
        return workUnit;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfWorkUnits() {
        return numberOfWorkUnits;
    }

    public int getExpectedConcurrency() {
        return expectedConcurrency;
    }

    public int getNumberOfSamples() {
        return numberOfSamples;
    }

    public double getMean() {
        return mean;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStdDev() {
        return stdDev;
    }
}
