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
    private final int numberOfWorkUnits;
    private final int concurrency;


    public CompletionStageWithExecutorServiceTest(UnitOfWork<?> unitOfWork, int concurrency, int numberOfWorkUnits) {
        this.unitOfWork = unitOfWork;
        this.numberOfWorkUnits = numberOfWorkUnits;
        this.concurrency = concurrency;

        fixedExecutor = Executors.newFixedThreadPool(concurrency);
    }

    @Override
    public TestResult test() {
        CompletableFuture<?>[] completableFutures = new CompletableFuture[numberOfWorkUnits];
        for (int i = 0; i < numberOfWorkUnits; i++) {
            completableFutures[i] = CompletableFuture.supplyAsync(unitOfWork::result, fixedExecutor);
        }

        TimingResult<Void> voidTimingResult = Timing.timeIt(() -> CompletableFuture.allOf(completableFutures).join());

        return new TestResult(this, unitOfWork, voidTimingResult.getDuration(), numberOfWorkUnits, concurrency);
    }

    @Override
    public void close() {
        fixedExecutor.shutdown();
    }
}
