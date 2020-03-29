package ch.guengel.concurrency.test;

public interface ConcurrencyTest extends AutoCloseable {
    String getTestName();

    TestResult test();
}
