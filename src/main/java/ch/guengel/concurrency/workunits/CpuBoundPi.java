package ch.guengel.concurrency.workunits;

public class CpuBoundPi implements UnitOfWork<Double> {
    @Override
    public Double result() {
        double denominator1 = -1;

        double piHalf = 1.0;
        double step = 2.0;
        for (double numerator = 2; numerator < 100000000.0; numerator += step) {
            denominator1 += step;

            piHalf *= (numerator / denominator1) * (numerator / (denominator1 + step));
        }

        return piHalf * 2.0;
    }

    @Override
    public void close() {
        // no impl
    }
}
