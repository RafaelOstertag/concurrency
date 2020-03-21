package ch.guengel.concurrency.timing;

import java.time.Duration;

public class TimingResult<T> {
    private T result;
    private Duration duration;

    public TimingResult(T result, Duration duration) {
        this.result = result;
        this.duration = duration;
    }

    public T getResult() {
        return result;
    }

    public Duration getDuration() {
        return duration;
    }
}
