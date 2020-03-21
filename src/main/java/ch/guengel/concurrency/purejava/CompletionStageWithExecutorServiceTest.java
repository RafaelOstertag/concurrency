package ch.guengel.concurrency.purejava;

import ch.guengel.concurrency.test.ConcurrencyTest;
import ch.guengel.concurrency.test.TestResult;
import ch.guengel.concurrency.timing.Timing;
import ch.guengel.concurrency.timing.TimingResult;
import ch.guengel.concurrency.workunits.UnitOfWork;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletionStageWithExecutorServiceTest implements ConcurrencyTest {
    private final UnitOfWork<?> unitOfWork;
    private final ExecutorService fixedExecutor;
    private final int repetitions;
    private final int concurrency;


    public CompletionStageWithExecutorServiceTest(UnitOfWork<?> unitOfWork, int concurrency, int repetitions) {
        this.unitOfWork = unitOfWork;
        this.repetitions = repetitions;
        this.concurrency = concurrency;

        fixedExecutor = Executors.newFixedThreadPool(concurrency);
    }

    @Override
    public TestResult test() {
        CompletableFuture<?>[] completableFutures = new CompletableFuture[repetitions];
        for (int i = 0; i < repetitions; i++) {
            completableFutures[i] = CompletableFuture.supplyAsync(unitOfWork::result, fixedExecutor);
        }

        TimingResult<Void> voidTimingResult = Timing.timeIt(() -> CompletableFuture.allOf(completableFutures).join());

        return new TestResult(this, unitOfWork, voidTimingResult.getDuration(), repetitions, concurrency);
    }

    @Override
    public void close() {
        fixedExecutor.shutdown();
    }
}
