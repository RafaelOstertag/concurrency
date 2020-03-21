package ch.guengel.concurrency.akka;

import akka.Done;
import akka.NotUsed;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;
import akka.stream.QueueOfferResult;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.SourceQueueWithComplete;
import ch.guengel.concurrency.test.ConcurrencyTest;
import ch.guengel.concurrency.test.TestResult;
import ch.guengel.concurrency.timing.Timing;
import ch.guengel.concurrency.timing.TimingResult;
import ch.guengel.concurrency.workunits.UnitOfWork;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StreamQueueTest implements ConcurrencyTest {
    private final SourceQueueWithComplete<Object> sourceQueue;
    private final Source<QueueOfferResult, NotUsed> source;
    private final int repetitions;
    private final UnitOfWork<?> unitOfWork;
    private final int concurrency;
    private final Materializer materializer;

    public StreamQueueTest(UnitOfWork<?> unitOfWork, int concurrency, int repetitions, Materializer materializer) {
        this.unitOfWork = unitOfWork;
        this.concurrency = concurrency;
        this.materializer = materializer;
        this.sourceQueue = Source.queue(concurrency, OverflowStrategy.backpressure())
                .map(n -> unitOfWork.result())
                .to(Sink.seq())
                .run(materializer);

        this.source = Source
                .from(IntStream
                        .range(0, repetitions)
                        .boxed()
                        .collect(Collectors.toList()))
                .map(sourceQueue::offer).mapAsync(concurrency, x -> x);
        this.repetitions = repetitions;
    }

    @Override
    public TestResult test() {
        TimingResult<Done> result = Timing.timeIt(() -> source.runWith(Sink.ignore(), materializer).toCompletableFuture().join());
        return new TestResult(this, unitOfWork, result.getDuration(), repetitions, concurrency);
    }

    @Override
    public void close() {
        // no impl
    }
}
