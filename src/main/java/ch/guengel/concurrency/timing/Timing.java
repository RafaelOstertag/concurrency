package ch.guengel.concurrency.timing;

import java.time.Duration;
import java.util.function.Supplier;

public final class Timing {
    private Timing() {
        // no instance
    }

    public static <T> TimingResult<T> timeIt(Supplier<T> supplier) {
        long t0 = System.currentTimeMillis();
        T result = supplier.get();
        long t1 = System.currentTimeMillis();
        return new TimingResult<>(result, Duration.ofMillis(t1 - t0));
    }
}
