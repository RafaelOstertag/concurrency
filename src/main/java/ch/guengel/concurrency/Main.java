package ch.guengel.concurrency;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import ch.guengel.concurrency.akka.*;
import ch.guengel.concurrency.purejava.CompletionServiceTest;
import ch.guengel.concurrency.purejava.CompletionStageTest;
import ch.guengel.concurrency.purejava.CompletionStageWithExecutorServiceTest;
import ch.guengel.concurrency.test.ConcurrencyTest;
import ch.guengel.concurrency.test.TestResult;
import ch.guengel.concurrency.workunits.HttpCall;
import ch.guengel.concurrency.workunits.Pi;
import ch.guengel.concurrency.workunits.UnitOfWork;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<UnitOfWork<?>> unitOfWorks = Arrays.asList(new Pi(), new HttpCall());
        unitOfWorks.forEach(Main::meassure);
    }

    private static void meassure(UnitOfWork<?> unitOfWork) {
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

        List<TestResult> testResults = tests.stream()
                .map(Main::runTest)
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
                new CompletionStageTest(unitOfWork, 10),
                new CompletionStageWithExecutorServiceTest(unitOfWork, 10, 10),
                //new CompletionStageWithExecutorService(unitOfWork, 2, 10),
                new CompletionServiceTest(unitOfWork, 10, 10),
                //new CompletionService(unitOfWork, 2, 10),
                new SimpleStreamTest(unitOfWork, 10, materializer),
                new SimpleStreamMapAsyncTest(unitOfWork, 10, 10, materializer),
                new SimpleStreamAsyncTest(unitOfWork, 10, materializer),
                new StreamQueueTest(unitOfWork, 10, 10, materializer),
                new StreamQueueAsyncTest(unitOfWork, 10, 10, materializer)
        );
    }

    private static TestResult runTest(ConcurrencyTest concurrencyTest) {
        try (ConcurrencyTest test = concurrencyTest) {
            return test.test();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(3);
            return null;
        }
    }
}
