package it.mycraft.powerlib.common.scheduler;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * One scheduling API across Bukkit, Folia, BungeeCord and Velocity. Time is expressed in
 * Minecraft ticks (20 ticks = 1 second); proxy implementations convert ticks to milliseconds.
 *
 * <p>The synchronous methods run on the main thread on Bukkit, on the global region scheduler on
 * Folia, and on the platform thread on the proxies. The async methods always run off the main thread.
 */
public interface PowerScheduler {

    /**
     * Runs the task synchronously as soon as possible.
     *
     * @param task the task to run
     * @return a handle to the scheduled task
     */
    Task run(Runnable task);

    /**
     * Runs the task synchronously after a delay.
     *
     * @param task       the task to run
     * @param delayTicks the delay before the first run, in ticks
     * @return a handle to the scheduled task
     */
    Task runLater(Runnable task, long delayTicks);

    /**
     * Runs the task synchronously and repeatedly.
     *
     * @param task        the task to run
     * @param delayTicks  the delay before the first run, in ticks
     * @param periodTicks the interval between runs, in ticks
     * @return a handle to the scheduled task
     */
    Task runTimer(Runnable task, long delayTicks, long periodTicks);

    /**
     * Runs the task off the main thread as soon as possible.
     *
     * @param task the task to run
     * @return a handle to the scheduled task
     */
    Task async(Runnable task);

    /**
     * Runs the task off the main thread after a delay.
     *
     * @param task       the task to run
     * @param delayTicks the delay before the first run, in ticks
     * @return a handle to the scheduled task
     */
    Task asyncLater(Runnable task, long delayTicks);

    /**
     * Runs the task off the main thread, repeatedly.
     *
     * @param task        the task to run
     * @param delayTicks  the delay before the first run, in ticks
     * @param periodTicks the interval between runs, in ticks
     * @return a handle to the scheduled task
     */
    Task asyncTimer(Runnable task, long delayTicks, long periodTicks);

    /**
     * Runs a blocking computation off the main thread and completes the returned future with its result.
     *
     * @param <T>      the type of the computed result
     * @param supplier the blocking computation to run
     * @return a future completed with the result, or completed exceptionally if the computation throws
     */
    default <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        CompletableFuture<T> future = new CompletableFuture<>();
        async(() -> {
            try {
                future.complete(supplier.get());
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }
}
