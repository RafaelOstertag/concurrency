package ch.guengel.concurrency.purejava;

import ch.guengel.concurrency.test.ConcurrencyTest;
import ch.guengel.concurrency.test.TestResult;
import ch.guengel.concurrency.timing.Timing;
import ch.guengel.concurrency.timing.TimingResult;
import ch.guengel.concurrency.workunits.UnitOfWork;

import java.util.concurrent.CompletableFuture;

public class CompletionStageTest implements ConcurrencyTest {
    private final UnitOfWork<?> unitOfWork;
    private final int numberOfWorkUnits;

    public CompletionStageTest(UnitOfWork<?> unitOfWork, int numberOfWorkUnits) {
        this.unitOfWork = unitOfWork;
        this.numberOfWorkUnits = numberOfWorkUnits;
    }

    @Override
    public String getTestName() {
        return unitOfWork.getClass().getSimpleName() + ":" + this.getClass().getSimpleName();
    }

    @Override
    public TestResult test() {
        CompletableFuture<?>[] completableFutures = new CompletableFuture[numberOfWorkUnits];
        for (int i = 0; i < numberOfWorkUnits; i++) {
            completableFutures[i] = CompletableFuture.supplyAsync(unitOfWork::result);
        }

        TimingResult<Void> voidTimingResult = Timing.timeIt(() -> CompletableFuture.allOf(completableFutures).join());
        return new TestResult(this, unitOfWork, voidTimingResult.getDuration(), numberOfWorkUnits, -1);
    }

    @Override
    public void close() {
// no impl
    }
}
