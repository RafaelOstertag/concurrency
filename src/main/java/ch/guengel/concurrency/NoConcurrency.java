package ch.guengel.concurrency;

import ch.guengel.concurrency.test.ConcurrencyTest;
import ch.guengel.concurrency.test.TestResult;
import ch.guengel.concurrency.timing.Timing;
import ch.guengel.concurrency.timing.TimingResult;
import ch.guengel.concurrency.workunits.UnitOfWork;

public class NoConcurrency implements ConcurrencyTest {
    private final UnitOfWork<?> unitOfWork;
    private final int numberOfWorkUnits;

    public NoConcurrency(UnitOfWork<?> unitOfWork, int numberOfWorkUnits) {
        this.unitOfWork = unitOfWork;
        this.numberOfWorkUnits = numberOfWorkUnits;
    }

    @Override
    public TestResult test() {
        TimingResult<Object> timingResult = Timing.timeIt(() -> {
                    Object object = null;
                    for (int i = 0; i < numberOfWorkUnits; i++) {
                        object = unitOfWork.result();
                    }
                    return object;
                }
        );
        return new TestResult(this, unitOfWork, timingResult.getDuration(), numberOfWorkUnits, 1);
    }

    @Override
    public void close() {
        // no impl
    }
}
