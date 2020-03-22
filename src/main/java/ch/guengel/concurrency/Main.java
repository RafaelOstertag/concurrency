package ch.guengel.concurrency;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import ch.guengel.concurrency.akka.*;
import ch.guengel.concurrency.purejava.CompletionServiceTest;
import ch.guengel.concurrency.purejava.CompletionStageTest;
import ch.guengel.concurrency.purejava.CompletionStageWithExecutorServiceTest;
import ch.guengel.concurrency.statistics.Statistics;
import ch.guengel.concurrency.statistics.TestStatistics;
import ch.guengel.concurrency.test.ConcurrencyTest;
import ch.guengel.concurrency.test.TestResult;
import ch.guengel.concurrency.workunits.HttpCall;
import ch.guengel.concurrency.workunits.Pi;
import ch.guengel.concurrency.workunits.UnitOfWork;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static final int NUMBER_OF_WORK_UNITS = 10;
    private static final int CONCURRENCY_LEVEL = 10;
    private static final int TEST_REPETITIONS = 10;

    public static void main(String[] args) {
        List<UnitOfWork<?>> unitOfWorks = Arrays.asList(new Pi(), new HttpCall());
        unitOfWorks.forEach(Main::measure);
    }

    private static void measure(UnitOfWork<?> unitOfWork) {
        System.err.println("Allow hotspot to kick in for " + unitOfWork.getClass().getSimpleName());
        for (int i = 0; i < 3; i++) {
            Object result = unitOfWork.result();
            if (result == null) {
                System.exit(2);
            }
        }

        final ActorSystem system = ActorSystem.create("QuickStart");
        final Materializer materializer = ActorMaterializer.create(system);

        List<ConcurrencyTest> tests = compileConcurrencyTests(unitOfWork, materializer);

        List<TestStatistics> testResults = tests.stream()
                .map(Main::runTest)
                .map(Statistics::compute)
                .collect(Collectors.toList());

        testResults.forEach(System.out::println);

        try (ReportWriter reportWriter = new ReportWriter(unitOfWork.getClass().getSimpleName() + ".csv")) {
            testResults.forEach(reportWriter::write);
        } catch (Exception e) {
            e.printStackTrace();
        }

        system.terminate();
    }

    @NotNull
    private static List<ConcurrencyTest> compileConcurrencyTests(UnitOfWork<?> unitOfWork, Materializer materializer) {
        return Arrays.asList(
                new NoConcurrency(unitOfWork),
                new CompletionStageTest(unitOfWork, NUMBER_OF_WORK_UNITS),
                new CompletionStageWithExecutorServiceTest(unitOfWork, CONCURRENCY_LEVEL, NUMBER_OF_WORK_UNITS),
                //new CompletionStageWithExecutorService(unitOfWork, 2, 10),
                new CompletionServiceTest(unitOfWork, CONCURRENCY_LEVEL, NUMBER_OF_WORK_UNITS),
                //new CompletionService(unitOfWork, 2, 10),
                new SimpleStreamTest(unitOfWork, NUMBER_OF_WORK_UNITS, materializer),
                new SimpleStreamMapAsyncTest(unitOfWork, CONCURRENCY_LEVEL, NUMBER_OF_WORK_UNITS, materializer),
                new SimpleStreamAsyncTest(unitOfWork, NUMBER_OF_WORK_UNITS, materializer),
                new StreamQueueTest(unitOfWork, CONCURRENCY_LEVEL, NUMBER_OF_WORK_UNITS, materializer),
                new StreamQueueAsyncTest(unitOfWork, CONCURRENCY_LEVEL, NUMBER_OF_WORK_UNITS, materializer)
        );
    }

    private static List<TestResult> runTest(ConcurrencyTest concurrencyTest) {
        try (ConcurrencyTest test = concurrencyTest) {
            ArrayList<TestResult> testResults = new ArrayList<>();
            for (int i = 0; i < TEST_REPETITIONS; i++) {
                testResults.add(test.test());
            }
            return testResults;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(3);
            return null;
        }
    }
}
