package ch.guengel.concurrency;

import ch.guengel.concurrency.statistics.TestStatistics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ReportWriter implements AutoCloseable {
    private static final String PRECISION = "%.2f";
    private final BufferedWriter bufferedWriter;

    public ReportWriter(String filename) throws IOException {
        bufferedWriter = new BufferedWriter(new FileWriter(filename));
        writeHeader();
    }

    private void writeHeader() throws IOException {
        bufferedWriter.write("Work Unit,Test Name,Number Work Units,Expected Concurrency,Sample Size,mean[ms],min[ms],max[ms],stddev[ms]");
        bufferedWriter.newLine();
    }

    public void write(TestStatistics testStatistics) {
        try {
            bufferedWriter.write(testStatistics.getWorkUnit());
            bufferedWriter.write(',');
            bufferedWriter.write(testStatistics.getName());
            bufferedWriter.write(',');
            bufferedWriter.write(Integer.toString(testStatistics.getNumberOfWorkUnits()));
            bufferedWriter.write(',');
            bufferedWriter.write(Integer.toString(testStatistics.getExpectedConcurrency()));
            bufferedWriter.write(',');
            bufferedWriter.write(Integer.toString(testStatistics.getNumberOfSamples()));
            bufferedWriter.write(',');
            bufferedWriter.write(String.format(PRECISION, testStatistics.getMean()));
            bufferedWriter.write(',');
            bufferedWriter.write(String.format(PRECISION, testStatistics.getMin()));
            bufferedWriter.write(',');
            bufferedWriter.write(String.format(PRECISION, testStatistics.getMax()));
            bufferedWriter.write(',');
            bufferedWriter.write(String.format(PRECISION, testStatistics.getStdDev()));
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        bufferedWriter.close();
    }
}
