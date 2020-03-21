package ch.guengel.concurrency.workunits;

public interface UnitOfWork<T> {
    T result();
}
