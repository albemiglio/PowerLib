package it.mycraft.powerlib.bukkit.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import it.mycraft.powerlib.bukkit.PowerLib;
import it.mycraft.powerlib.common.scheduler.PowerScheduler;
import it.mycraft.powerlib.common.scheduler.Task;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Bukkit/Paper/Folia scheduler. On Folia it routes through the global region and async schedulers;
 * everywhere else through the classic BukkitScheduler.
 */
public class BukkitScheduler implements PowerScheduler {

    private static final boolean FOLIA = foliaPresent();
    private static final long MS_PER_TICK = 50L;

    private static boolean foliaPresent() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static Plugin plugin() {
        return PowerLib.getPlugin();
    }

    @Override
    public Task run(Runnable task) {
        return FOLIA ? folia(Bukkit.getGlobalRegionScheduler().run(plugin(), consumer(task)))
                : bukkit(Bukkit.getScheduler().runTask(plugin(), task));
    }

    @Override
    public Task runLater(Runnable task, long delayTicks) {
        return FOLIA ? folia(Bukkit.getGlobalRegionScheduler().runDelayed(plugin(), consumer(task), Math.max(1, delayTicks)))
                : bukkit(Bukkit.getScheduler().runTaskLater(plugin(), task, delayTicks));
    }

    @Override
    public Task runTimer(Runnable task, long delayTicks, long periodTicks) {
        return FOLIA ? folia(Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin(), consumer(task), Math.max(1, delayTicks), Math.max(1, periodTicks)))
                : bukkit(Bukkit.getScheduler().runTaskTimer(plugin(), task, delayTicks, periodTicks));
    }

    @Override
    public Task async(Runnable task) {
        return FOLIA ? folia(Bukkit.getAsyncScheduler().runNow(plugin(), consumer(task)))
                : bukkit(Bukkit.getScheduler().runTaskAsynchronously(plugin(), task));
    }

    @Override
    public Task asyncLater(Runnable task, long delayTicks) {
        return FOLIA ? folia(Bukkit.getAsyncScheduler().runDelayed(plugin(), consumer(task), delayTicks * MS_PER_TICK, TimeUnit.MILLISECONDS))
                : bukkit(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin(), task, delayTicks));
    }

    @Override
    public Task asyncTimer(Runnable task, long delayTicks, long periodTicks) {
        return FOLIA ? folia(Bukkit.getAsyncScheduler().runAtFixedRate(plugin(), consumer(task), Math.max(1, delayTicks) * MS_PER_TICK, Math.max(1, periodTicks) * MS_PER_TICK, TimeUnit.MILLISECONDS))
                : bukkit(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin(), task, delayTicks, periodTicks));
    }

    private static Consumer<ScheduledTask> consumer(Runnable task) {
        return scheduled -> task.run();
    }

    private static Task bukkit(BukkitTask bukkitTask) {
        return handle(bukkitTask::cancel);
    }

    private static Task folia(ScheduledTask scheduledTask) {
        return handle(scheduledTask::cancel);
    }

    private static Task handle(Runnable cancelAction) {
        return new Task() {
            private volatile boolean cancelled;

            @Override
            public void cancel() {
                if (!cancelled) {
                    cancelAction.run();
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
