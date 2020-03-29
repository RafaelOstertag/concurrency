package ch.guengel.concurrency.workunits;

public interface UnitOfWork<T> extends AutoCloseable {
    T result();
}
