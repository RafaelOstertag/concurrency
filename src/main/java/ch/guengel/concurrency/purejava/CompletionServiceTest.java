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
    private final int repetitions;

    public CompletionServiceTest(UnitOfWork<?> unitOfWork, int concurrency, int repetitions) {
        this.unitOfWork = unitOfWork;
        this.concurrency = concurrency;
        this.repetitions = repetitions;

        executorService = Executors.newFixedThreadPool(concurrency);
        completionService = new ExecutorCompletionService<>(executorService);
    }

    @Override
    public TestResult test() {
        Future<?>[] results = new Future[repetitions];

        TimingResult<Boolean> booleanTimingResult = Timing.timeIt(() -> {
            for (int i = 0; i < repetitions; i++) {
                results[i] = completionService.submit(unitOfWork::result);
            }

            for (int i = 0; i < repetitions; i++) {
                try {
                    results[i].get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            return true;
        });

        return new TestResult(this, unitOfWork, booleanTimingResult.getDuration(), repetitions, concurrency);
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
