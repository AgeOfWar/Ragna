package com.github.ageofwar.solex;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.function.LongConsumer;

public interface Engine extends Runnable, AutoCloseable {
    void init();

    @Override
    void run();

    void close();

    Window createWindow(WindowConfiguration configuration);

    Executor executor();
    Executor asyncExecutor();

    interface Executor {
        Future<?> execute(Runnable task);
        <T> ScheduledFuture<T> execute(Callable<T> task);
        ScheduledFuture<?> schedule(Runnable task, long delay);
        <T> ScheduledFuture<T> schedule(Callable<T> task, long delay);
        ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long delay, long period);
        ScheduledFuture<?> scheduleAtFixedRate(LongConsumer task, long delay, long period);
    }
}
