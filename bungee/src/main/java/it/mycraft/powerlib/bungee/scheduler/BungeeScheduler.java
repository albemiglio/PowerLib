package it.mycraft.powerlib.bungee.scheduler;

import it.mycraft.powerlib.bungee.PowerLib;
import it.mycraft.powerlib.common.scheduler.PowerScheduler;
import it.mycraft.powerlib.common.scheduler.Task;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.util.concurrent.TimeUnit;

/**
 * BungeeCord scheduler. The proxy has no main thread, so the synchronous methods behave like the
 * asynchronous ones; tick delays are converted to milliseconds.
 */
public class BungeeScheduler implements PowerScheduler {

    private static final long MS_PER_TICK = 50L;

    private static TaskScheduler scheduler() {
        return ProxyServer.getInstance().getScheduler();
    }

    private static Plugin plugin() {
        return PowerLib.getPlugin();
    }

    @Override
    public Task run(Runnable task) {
        return wrap(scheduler().runAsync(plugin(), task));
    }

    @Override
    public Task runLater(Runnable task, long delayTicks) {
        return wrap(scheduler().schedule(plugin(), task, delayTicks * MS_PER_TICK, TimeUnit.MILLISECONDS));
    }

    @Override
    public Task runTimer(Runnable task, long delayTicks, long periodTicks) {
        return wrap(scheduler().schedule(plugin(), task, delayTicks * MS_PER_TICK, periodTicks * MS_PER_TICK, TimeUnit.MILLISECONDS));
    }

    @Override
    public Task async(Runnable task) {
        return run(task);
    }

    @Override
    public Task asyncLater(Runnable task, long delayTicks) {
        return runLater(task, delayTicks);
    }

    @Override
    public Task asyncTimer(Runnable task, long delayTicks, long periodTicks) {
        return runTimer(task, delayTicks, periodTicks);
    }

    private static Task wrap(ScheduledTask scheduledTask) {
        return new Task() {
            private volatile boolean cancelled;

            @Override
            public void cancel() {
                if (!cancelled) {
                    scheduledTask.cancel();
                    cancelled = true;
                }
            }

            @Override
            public boolean isCancelled() {
                return cancelled;
            }
        };
    }
}
