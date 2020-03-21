package ch.guengel.concurrency;

import ch.guengel.concurrency.test.TestResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ReportWriter implements AutoCloseable {
    private final BufferedWriter bufferedWriter;

    public ReportWriter(String filename) throws IOException {
        bufferedWriter = new BufferedWriter(new FileWriter(filename));
        writeHeader();
    }

    private void writeHeader() throws IOException {
        bufferedWriter.write("Work Unit,Test Name,Time[ms],Number of Tests,Concurrency");
        bufferedWriter.newLine();
    }

    public void write(TestResult testResult) {
        try {
            bufferedWriter.write(testResult.getWorkUnit());
            bufferedWriter.write(',');
            bufferedWriter.write(testResult.getName());
            bufferedWriter.write(',');
            bufferedWriter.write(Long.toString(testResult.getDuration().toMillis()));
            bufferedWriter.write(',');
            bufferedWriter.write(Integer.toString(testResult.getRepetitions()));
            bufferedWriter.write(',');
            bufferedWriter.write(Integer.toString(testResult.getConcurrency()));
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
