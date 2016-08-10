package org.sai.tools.throttler.facade;

import lombok.Data;
import org.sai.tools.throttler.model.DataTuple;
import org.sai.tools.throttler.model.TooManyInvocationsException;

import java.util.Comparator;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Data
public class InvokerFacade<I, O> {

    private final String throttlerId;
    private final PriorityBlockingQueue<DataTuple<I, O>> queue;
    private final Comparator<DataTuple<I, O>> priorityComparatorFunction;
    private final Integer maxCapacity;
    private final Consumer<TooManyInvocationsException> withTooManyInvocationsExceptionCallback;
    private final Integer withNoOfInvocationsParallel;
    private final ExecutorService executor;

    public InvokerFacade(final String throttlerId, final Comparator<DataTuple<I, O>> priorityComparatorFunction, final Integer maxCapacity, final Consumer<TooManyInvocationsException> withTooManyInvocationsExceptionCallback, final Integer withNoOfInvocationsParallel) {
        this.throttlerId = throttlerId;
        this.priorityComparatorFunction = priorityComparatorFunction;
        this.maxCapacity = maxCapacity;
        this.withTooManyInvocationsExceptionCallback = withTooManyInvocationsExceptionCallback;
        this.withNoOfInvocationsParallel = withNoOfInvocationsParallel;

        // Computed.
        this.queue = new PriorityBlockingQueue<>(2, priorityComparatorFunction);
        this.executor = Executors.newFixedThreadPool(withNoOfInvocationsParallel);

        // Cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down executor");
            this.executor.shutdownNow();
        }));
    }

    public void execute(final I input, final Function<I, O> functionTobeinvoked, final Consumer<O> resultCallback) {
        DataTuple<I, O> dataTuple = new DataTuple<>(input, resultCallback);
        if (queue.size() >= maxCapacity) {
            withTooManyInvocationsExceptionCallback.accept(new TooManyInvocationsException("Throttler id: " + throttlerId + ". Queue is full. " + queue.size()));
        } else {
            queue.put(dataTuple);
            executor.submit(new ExecutionCommandRunnable<>(queue, functionTobeinvoked));
        }
    }

    public Future<O> executeWithNoPriority(final I input, final Function<I, O> functionTobeinvoked) {
        DataTuple<I, O> dataTuple = new DataTuple<>(input, null);
        if (queue.size() >= maxCapacity) {
            withTooManyInvocationsExceptionCallback.accept(new TooManyInvocationsException("Throttler id: " + throttlerId + ". Queue is full. " + queue.size()));
            // FIXME
            return null;
        } else {
            queue.put(dataTuple);
            return executor.submit(new ExecutionCommandCallable<>(queue, functionTobeinvoked));
        }
    }

    private static class ExecutionCommandRunnable<I, O> implements Runnable {
        private final BlockingQueue<DataTuple<I, O>> queue;
        private final Function<I, O> functionTobeinvoked;

        private ExecutionCommandRunnable(final BlockingQueue<DataTuple<I, O>> queue, final Function<I, O> functionTobeinvoked) {
            this.queue = queue;
            this.functionTobeinvoked = functionTobeinvoked;
        }


        @Override
        public void run() {
            try {
                DataTuple<I, O> dataTuple = queue.take();
                dataTuple.getResultCallback().accept(functionTobeinvoked.apply(dataTuple.getData()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ExecutionCommandCallable<I, O> implements Callable<O> {
        private final BlockingQueue<DataTuple<I, O>> queue;
        private final Function<I, O> functionTobeinvoked;

        private ExecutionCommandCallable(final BlockingQueue<DataTuple<I, O>> queue, final Function<I, O> functionTobeinvoked) {
            this.queue = queue;
            this.functionTobeinvoked = functionTobeinvoked;
        }


        @Override
        public O call() throws Exception {
            DataTuple<I, O> dataTuple = queue.take();
            return functionTobeinvoked.apply(dataTuple.getData());
        }
    }
}