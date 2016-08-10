package org.sai.tools.throttler.factory;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.sai.tools.throttler.facade.InvokerFacade;
import org.sai.tools.throttler.model.DataTuple;
import org.sai.tools.throttler.model.InvokerConfig;
import org.sai.tools.throttler.model.TooManyInvocationsException;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Created by saipkri on 09/08/16.
 */
public final class InvokerFactory {

    private static final InvokerFactory INSTANCE = new InvokerFactory();

    private InvokerFactory() {
    }

    public static InvokerFactory getInstance() {
        return INSTANCE;
    }

    @Data
    @AllArgsConstructor
    private static final class Holder<I, O> {
        private final InvokerFacade<I, O> invokerFacade;
    }

    private static final ConcurrentHashMap<String, Holder> CACHE = new ConcurrentHashMap<>();

    public <I, O> InvokerFacade<I, O> fromConfig(final InvokerConfig<I, O> invokerConfig) {
        String throttlerId = invokerConfig.getId();
        Integer maxCapacity = invokerConfig.getMaxCapacity();
        Consumer<TooManyInvocationsException> withTooManyInvocationsExceptionCallback = invokerConfig.getWithTooManyInvocationsExceptionCallback();
        Comparator<DataTuple<I, O>> withPriorityComputeFunction = invokerConfig.getWithPriorityComputeFunction();
        Integer withNoOfInvocationsParallel = invokerConfig.getWithNoOfInvocationsParallel();
        Holder<I, O> holder = CACHE.computeIfAbsent(throttlerId, key -> new Holder<>(new InvokerFacade<>(throttlerId, withPriorityComputeFunction, maxCapacity, withTooManyInvocationsExceptionCallback, withNoOfInvocationsParallel)));
        return holder.getInvokerFacade();
    }
}
