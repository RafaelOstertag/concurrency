package ch.guengel.concurrency;

import ch.guengel.concurrency.test.ConcurrencyTest;
import ch.guengel.concurrency.test.TestResult;
import ch.guengel.concurrency.timing.Timing;
import ch.guengel.concurrency.timing.TimingResult;
import ch.guengel.concurrency.workunits.UnitOfWork;

public class OneUnitOfWork implements ConcurrencyTest {
    private final UnitOfWork<?> unitOfWork;

    public OneUnitOfWork(UnitOfWork<?> unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public String getTestName() {
        return unitOfWork.getClass().getSimpleName() + ":" + this.getClass().getSimpleName();
    }

    @Override
    public TestResult test() {
        TimingResult<Object> timingResult = Timing.timeIt(unitOfWork::result);
        return new TestResult(this, unitOfWork, timingResult.getDuration(), 1, 1);
    }

    @Override
    public void close() {
        // no impl
    }
}
