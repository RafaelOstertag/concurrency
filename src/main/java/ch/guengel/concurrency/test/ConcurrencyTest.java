package ch.guengel.concurrency.test;

public interface ConcurrencyTest extends AutoCloseable {
    TestResult test();
}
