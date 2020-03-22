package ch.guengel.concurrency.statistics;

import ch.guengel.concurrency.test.TestResult;

import java.util.List;

public final class Statistics {
    private Statistics() {
        // no instance
    }

    public static TestStatistics compute(List<TestResult> testResults) {
        if (testResults.isEmpty()) {
            return null;
        }

        double min = min(testResults);
        double max = max(testResults);
        double mean = mean(testResults);
        double stdDev = stdDev(testResults, mean);

        TestResult firstTestResult = testResults.get(0);
        return new TestStatistics(
                firstTestResult.getWorkUnit(),
                firstTestResult.getName(),
                firstTestResult.getNumberOfWorkUnits(),
                firstTestResult.getExpectedConcurrency(),
                testResults.size(),
                mean,
                min,
                max,
                stdDev
        );
    }

    private static double stdDev(List<TestResult> testResults, double mean) {
        if (testResults.size() < 1) {
            return 0.0;
        }

        double sumSquaredDifference = 0.0;

        for (TestResult testResult : testResults) {
            sumSquaredDifference += Math.pow(testResult.getDuration().toMillis() - mean, 2);
        }

        return Math.sqrt(sumSquaredDifference / (testResults.size() - 1));
    }

    private static double min(List<TestResult> testResults) {
        return testResults.stream()
                .map(x -> x.getDuration().toMillis())
                .mapToDouble(Long::doubleValue)
                .min()
                .orElse(Double.MAX_VALUE);
    }

    private static double max(List<TestResult> testResults) {
        return testResults.stream()
                .map(x -> x.getDuration().toMillis())
                .mapToDouble(Long::doubleValue)
                .max()
                .orElse(Double.MAX_VALUE);
    }

    private static double mean(List<TestResult> testResults) {
        return testResults.stream()
                .map(x -> x.getDuration().toMillis())
                .mapToDouble(Long::doubleValue)
                .average()
                .orElse(Double.MAX_VALUE);
    }
}
