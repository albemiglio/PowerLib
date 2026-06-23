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

    Task run(Runnable task);

    Task runLater(Runnable task, long delayTicks);

    Task runTimer(Runnable task, long delayTicks, long periodTicks);

    Task async(Runnable task);

    Task asyncLater(Runnable task, long delayTicks);

    Task asyncTimer(Runnable task, long delayTicks, long periodTicks);

    /**
     * Runs a blocking computation off the main thread and completes the returned future with its result.
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
