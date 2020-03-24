package ch.guengel.concurrency.akka;

import akka.NotUsed;
import akka.stream.FlowShape;
import akka.stream.Materializer;
import akka.stream.UniformFanInShape;
import akka.stream.UniformFanOutShape;
import akka.stream.javadsl.*;
import ch.guengel.concurrency.test.ConcurrencyTest;
import ch.guengel.concurrency.test.TestResult;
import ch.guengel.concurrency.timing.Timing;
import ch.guengel.concurrency.timing.TimingResult;
import ch.guengel.concurrency.workunits.UnitOfWork;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AkkaParallelTest implements ConcurrencyTest {
    private final Source<Object, NotUsed> source;
    private final int numberOfWorkUnits;
    private final Materializer materializer;
    private final UnitOfWork<?> unitOfWork;
    private final int concurrency;

    public AkkaParallelTest(UnitOfWork<?> unitOfWork, int concurrency, int numberOfWorkUnits, Materializer materializer) {
        this.unitOfWork = unitOfWork;
        this.concurrency = concurrency;
        this.materializer = materializer;
        this.source = Source
                .from(IntStream
                        .range(0, numberOfWorkUnits)
                        .boxed()
                        .collect(Collectors.toList()))
                .via(parallelFlow(Flow.of(Integer.class).map(n -> unitOfWork.result()), concurrency));
        this.numberOfWorkUnits = numberOfWorkUnits;
    }

    private Flow<Integer, Object, NotUsed> parallelFlow(Flow<Integer, Object, NotUsed> innerFlow, int concurrency) {
        return Flow.fromGraph(
                GraphDSL.create(
                        b -> {
                            UniformFanInShape<Object, Object> merge = b.add(Merge.create(concurrency));
                            UniformFanOutShape<Integer, Integer> dispatcher = b.add(Balance.create(concurrency));

                            for (int i = 0; i < concurrency; i++) {
                                b.from(dispatcher.out(i))
                                        .via(b.add(innerFlow.async()))
                                        .toInlet(merge.in(i));
                            }

                            return FlowShape.of(dispatcher.in(), merge.out());
                        }
                )
        );
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
