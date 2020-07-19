package ch.guengel.concurrency.purejava;

import ch.guengel.concurrency.test.ConcurrencyTest;
import ch.guengel.concurrency.test.TestResult;
import ch.guengel.concurrency.timing.Timing;
import ch.guengel.concurrency.timing.TimingResult;
import ch.guengel.concurrency.workunits.UnitOfWork;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StreamsTest implements ConcurrencyTest {
    private final ExecutorService executorService;
    private final UnitOfWork<?> unitOfWork;
    private final int concurrency;
    private final int numberOfWorkUnits;

    public StreamsTest(UnitOfWork<?> unitOfWork, int concurrency, int numberOfWorkUnits) {
        this.executorService = new ForkJoinPool(concurrency);
        this.unitOfWork = unitOfWork;
        this.concurrency = concurrency;
        this.numberOfWorkUnits = numberOfWorkUnits;
    }

    @Override
    public String getTestName() {
        return unitOfWork.getClass().getSimpleName() + ":" + this.getClass().getSimpleName();
    }

    @Override
    public TestResult test() {
        TimingResult<List<Integer>> timingResult = Timing.timeIt(() ->
                {
                    try {
                        return executorService.submit(() ->
                                IntStream
                                        .range(0, numberOfWorkUnits)
                                        .boxed()
                                        .parallel()
                                        .map(this::executeTest)
                                        .collect(Collectors.toList())
                        ).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
        );

        return new TestResult(this, unitOfWork, timingResult.getDuration(), numberOfWorkUnits, concurrency);
    }

    private int executeTest(int i) {
        Object result = unitOfWork.result();
        return result.hashCode();
    }

    @Override
    public void close() throws Exception {
        executorService.shutdown();
    }
}
