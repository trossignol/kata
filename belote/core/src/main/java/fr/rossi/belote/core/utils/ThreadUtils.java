package fr.rossi.belote.core.utils;

import fr.rossi.belote.core.exception.TechnicalException;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class ThreadUtils {

    private ThreadUtils() {
        super();
    }

    public static void parallel(List<Runnable> callables) {
        parallel(callables, false);
    }

    public static void parallel(List<Runnable> callables, boolean virtual) {
        try (var executor = virtual
                ? Executors.newVirtualThreadPerTaskExecutor()
                : Executors.newFixedThreadPool(8)) {
            try {
                callables.forEach(executor::submit);
                executor.shutdown();
                if (!executor.awaitTermination(100, TimeUnit.SECONDS)) executor.shutdownNow();
            } catch (InterruptedException e) {
                throw new TechnicalException("Error executing tasks in parallel", e);
            } finally {
                executor.shutdownNow();
            }
        }
    }
}
