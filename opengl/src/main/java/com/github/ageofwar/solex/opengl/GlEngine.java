package com.github.ageofwar.solex.opengl;

import com.github.ageofwar.solex.Engine;
import com.github.ageofwar.solex.Window;
import com.github.ageofwar.solex.WindowConfiguration;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.*;
import java.util.function.LongConsumer;

import static org.lwjgl.glfw.GLFW.*;

public class GlEngine implements Engine {
    private final EngineExecutorService executor;
    private final AsyncExecutorService asyncExecutor;
    private final ArrayList<GlWindow> windows;

    public static GlEngine create() {
        var engine = new GlEngine();
        engine.init();
        return engine;
    }

    public GlEngine() {
        executor = new EngineExecutorService();
        asyncExecutor = new AsyncExecutorService(Executors.newScheduledThreadPool(8, r -> {
            var thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }));
        windows = new ArrayList<>();
    }

    @Override
    public void init() {
        if (!glfwInit()) throw new RuntimeException("Unable to initialize GLFW");
        GLFWErrorCallback.createPrint(System.err).set();
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            executor.executeRemaining();
            glfwPollEvents();
            windows.removeIf(window -> {
                if (window.shouldClose()) {
                    window.close();
                    return true;
                }
                var time = System.nanoTime();
                window.render(time);
                return false;
            });
        }
    }

    @Override
    public void close() {
        for (var window : windows) {
            window.close();
        }
        glfwTerminate();
    }

    @Override
    public Window createWindow(WindowConfiguration configuration) {
        var id = glfwCreateWindow(configuration.width(), configuration.height(), configuration.title(), 0, 0);
        if (id == 0) throw new RuntimeException("Unable to create window");
        var window = new GlWindow(id, this);
        this.windows.add(window);
        window.init();
        return window;
    }

    @Override
    public Executor executor() {
        return executor;
    }

    @Override
    public Executor asyncExecutor() {
        return asyncExecutor;
    }

    private static class EngineExecutorService implements Executor {
        PriorityQueue<Task<?>> tasks = new PriorityQueue<>();

        public synchronized void executeRemaining() {
            while (!tasks.isEmpty()) {
                var now = System.nanoTime();
                var task = tasks.peek();
                assert task != null;
                if (task.time <= now) {
                    task.setExecutionTime(now);
                    tasks.poll().run();
                } else {
                    break;
                }
            }
        }

        @Override
        public synchronized Future<?> execute(Runnable task) {
            var future = new Task<>(() -> {
                task.run();
                return null;
            });
            tasks.add(future);
            return future;
        }

        @Override
        public synchronized <T> ScheduledFuture<T> execute(Callable<T> task) {
            var future = new Task<>(task);
            tasks.add(future);
            return future;
        }

        @Override
        public synchronized ScheduledFuture<?> schedule(Runnable task, long delay) {
            var future = new Task<>(task, System.nanoTime() + delay);
            tasks.add(future);
            return future;
        }

        @Override
        public synchronized <T> ScheduledFuture<T> schedule(Callable<T> task, long delay) {
            var future = new Task<>(task, System.nanoTime() + delay);
            tasks.add(future);
            return future;
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long delay, long period) {
            return scheduleAtFixedRate((t) -> task.run(), System.nanoTime(), delay, period);
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(LongConsumer task, long delay, long period) {
            return scheduleAtFixedRate(task, System.nanoTime(), delay, period);
        }

        private synchronized ScheduledFuture<?> scheduleAtFixedRate(LongConsumer task, long now, long delay, long period) {
            var future = new Task<>((t) -> {
                scheduleAtFixedRate(task, now + delay, period, period);
                task.accept(t);
            }, now + delay);
            tasks.add(future);
            return future;
        }

        private static class Task<T> implements Runnable, Comparable<Delayed>, ScheduledFuture<T> {
            private final FutureTask<T> task;
            private final long time;
            private long executionTime;

            public Task(Runnable runnable, long time) {
                this.task = new FutureTask<>(runnable, null);
                this.time = time;
            }

            public Task(Callable<T> runnable, long time) {
                this.task = new FutureTask<>(runnable);
                this.time = time;
            }

            public Task(Callable<T> runnable) {
                this(runnable, 0);
            }

            public Task(LongConsumer runnable, long time) {
                this.task = new FutureTask<>(() -> runnable.accept(executionTime), null);
                this.time = time;
            }

            private void setExecutionTime(long executionTime) {
                this.executionTime = executionTime;
            }

            @Override
            public long getDelay(TimeUnit unit) {
                return unit.convert(time - System.nanoTime(), TimeUnit.NANOSECONDS);
            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return task.isDone();
            }

            @Override
            public T get() throws InterruptedException, ExecutionException {
                return task.get();
            }

            @Override
            public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return task.get(timeout, unit);
            }

            @Override
            public void run() {
                task.run();
            }

            @Override
            public int compareTo(Delayed o) {
                return Long.compare(time, ((Task<?>) o).time);
            }
        }
    }

    private static class AsyncExecutorService implements Executor {
        private final ScheduledExecutorService executor;

        public AsyncExecutorService(ScheduledExecutorService executor) {
            this.executor = executor;
        }

        @Override
        public Future<?> execute(Runnable task) {
            return executor.submit(task);
        }

        @Override
        public <T> ScheduledFuture<T> execute(Callable<T> task) {
            return executor.schedule(task, 0L, TimeUnit.NANOSECONDS);
        }

        @Override
        public ScheduledFuture<?> schedule(Runnable task, long delay) {
            return executor.schedule(task, delay, TimeUnit.NANOSECONDS);
        }

        @Override
        public <T> ScheduledFuture<T> schedule(Callable<T> task, long delay) {
            return executor.schedule(task, delay, TimeUnit.NANOSECONDS);
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long delay, long period) {
            return executor.scheduleAtFixedRate(task, delay, period, TimeUnit.NANOSECONDS);
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(LongConsumer task, long delay, long period) {
            return executor.scheduleAtFixedRate(() -> task.accept(System.nanoTime()), delay, period, TimeUnit.NANOSECONDS);
        }
    }
}
