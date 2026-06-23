package it.mycraft.powerlib.velocity.scheduler;

import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.Scheduler;
import it.mycraft.powerlib.common.scheduler.PowerScheduler;
import it.mycraft.powerlib.common.scheduler.Task;
import it.mycraft.powerlib.velocity.PowerLib;

import java.util.concurrent.TimeUnit;

/**
 * Velocity scheduler. The proxy has no main thread, so the synchronous methods behave like the
 * asynchronous ones; tick delays are converted to milliseconds.
 */
public class VelocityScheduler implements PowerScheduler {

    private static final long MS_PER_TICK = 50L;

    private static Scheduler scheduler() {
        return PowerLib.getProxy().getScheduler();
    }

    private static Object plugin() {
        return PowerLib.getPlugin();
    }

    @Override
    public Task run(Runnable task) {
        return wrap(scheduler().buildTask(plugin(), task).schedule());
    }

    @Override
    public Task runLater(Runnable task, long delayTicks) {
        return wrap(scheduler().buildTask(plugin(), task)
                .delay(delayTicks * MS_PER_TICK, TimeUnit.MILLISECONDS)
                .schedule());
    }

    @Override
    public Task runTimer(Runnable task, long delayTicks, long periodTicks) {
        return wrap(scheduler().buildTask(plugin(), task)
                .delay(delayTicks * MS_PER_TICK, TimeUnit.MILLISECONDS)
                .repeat(periodTicks * MS_PER_TICK, TimeUnit.MILLISECONDS)
                .schedule());
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
