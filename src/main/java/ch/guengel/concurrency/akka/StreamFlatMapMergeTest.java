package ch.guengel.concurrency.akka;

import akka.NotUsed;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import ch.guengel.concurrency.test.ConcurrencyTest;
import ch.guengel.concurrency.test.TestResult;
import ch.guengel.concurrency.timing.Timing;
import ch.guengel.concurrency.timing.TimingResult;
import ch.guengel.concurrency.workunits.UnitOfWork;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StreamFlatMapMergeTest implements ConcurrencyTest {
    private final Source<Object, NotUsed> source;
    private final int numberOfWorkUnits;
    private final UnitOfWork<?> unitOfWork;
    private final int concurrency;
    private final Materializer materializer;

    public StreamFlatMapMergeTest(UnitOfWork<?> unitOfWork, int concurrency, int numberOfWorkUnits, Materializer materializer) {
        this.unitOfWork = unitOfWork;
        this.concurrency = concurrency;
        this.materializer = materializer;
        this.source = Source
                .from(IntStream
                        .range(0, numberOfWorkUnits)
                        .boxed()
                        .collect(Collectors.toList()))
                .flatMapMerge(concurrency, n ->
                        Source
                                .single(n)
                                .map(nn -> unitOfWork.result())
                );
        this.numberOfWorkUnits = numberOfWorkUnits;
    }

    @Override
    public String getTestName() {
        return unitOfWork.getClass().getSimpleName() + ":" + this.getClass().getSimpleName();
    }

    @Override
    public TestResult test() {
        TimingResult<List<Object>> result = Timing.timeIt(() -> source.runWith(Sink.seq(), materializer).toCompletableFuture().join());
        return new TestResult(this, unitOfWork, result.getDuration(), numberOfWorkUnits, concurrency);
    }

    @Override
    public void close() {
        // no impl
    }
}
