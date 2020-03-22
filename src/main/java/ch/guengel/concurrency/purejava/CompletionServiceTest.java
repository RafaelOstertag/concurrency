package ch.guengel.concurrency.purejava;

import ch.guengel.concurrency.test.ConcurrencyTest;
import ch.guengel.concurrency.test.TestResult;
import ch.guengel.concurrency.timing.Timing;
import ch.guengel.concurrency.timing.TimingResult;
import ch.guengel.concurrency.workunits.UnitOfWork;

import java.util.concurrent.*;

public class CompletionServiceTest implements ConcurrencyTest {
    private final ExecutorService executorService;
    private final java.util.concurrent.CompletionService<Object> completionService;
    private final UnitOfWork<?> unitOfWork;
    private final int concurrency;
    private final int numberOfWorkUnits;

    public CompletionServiceTest(UnitOfWork<?> unitOfWork, int concurrency, int numberOfWorkUnits) {
        this.unitOfWork = unitOfWork;
        this.concurrency = concurrency;
        this.numberOfWorkUnits = numberOfWorkUnits;

        executorService = Executors.newFixedThreadPool(concurrency);
        completionService = new ExecutorCompletionService<>(executorService);
    }

    @Override
    public TestResult test() {
        Future<?>[] results = new Future[numberOfWorkUnits];

        TimingResult<Boolean> booleanTimingResult = Timing.timeIt(() -> {
            for (int i = 0; i < numberOfWorkUnits; i++) {
                results[i] = completionService.submit(unitOfWork::result);
            }

            for (int i = 0; i < numberOfWorkUnits; i++) {
                try {
                    results[i].get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            return true;
        });

        return new TestResult(this, unitOfWork, booleanTimingResult.getDuration(), numberOfWorkUnits, concurrency);
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
