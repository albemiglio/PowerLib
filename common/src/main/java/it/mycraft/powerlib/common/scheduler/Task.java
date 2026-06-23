package it.mycraft.powerlib.common.scheduler;

/**
 * A handle to a scheduled task, usable to cancel it across every platform.
 */
public interface Task {

    void cancel();

    boolean isCancelled();
}
