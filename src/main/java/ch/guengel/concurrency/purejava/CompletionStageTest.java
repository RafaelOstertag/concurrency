package ch.guengel.concurrency.purejava;

import ch.guengel.concurrency.test.ConcurrencyTest;
import ch.guengel.concurrency.test.TestResult;
import ch.guengel.concurrency.timing.Timing;
import ch.guengel.concurrency.timing.TimingResult;
import ch.guengel.concurrency.workunits.UnitOfWork;

import java.util.concurrent.CompletableFuture;

public class CompletionStageTest implements ConcurrencyTest {
    private final UnitOfWork<?> unitOfWork;
    private final int repetitions;

    public CompletionStageTest(UnitOfWork<?> unitOfWork, int repetitions) {
        this.unitOfWork = unitOfWork;
        this.repetitions = repetitions;
    }

    @Override
    public TestResult test() {
        CompletableFuture<?>[] completableFutures = new CompletableFuture[repetitions];
        for (int i = 0; i < repetitions; i++) {
            completableFutures[i] = CompletableFuture.supplyAsync(unitOfWork::result);
        }

        TimingResult<Void> voidTimingResult = Timing.timeIt(() -> CompletableFuture.allOf(completableFutures).join());
        return new TestResult(this, unitOfWork, voidTimingResult.getDuration(), repetitions, -1);
    }

    @Override
    public void close() {
// no impl
    }
}
