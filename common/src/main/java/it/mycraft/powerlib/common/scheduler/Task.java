package it.mycraft.powerlib.common.scheduler;

/**
 * A handle to a scheduled task, usable to cancel it across every platform.
 */
public interface Task {

    /** Cancels the task so it will not run again. */
    void cancel();

    /** @return {@code true} if the task has been cancelled */
    boolean isCancelled();
}
