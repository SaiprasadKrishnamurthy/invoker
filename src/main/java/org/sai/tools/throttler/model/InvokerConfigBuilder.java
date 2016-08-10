package org.sai.tools.throttler.model;

import lombok.Getter;

import java.util.Comparator;
import java.util.function.Consumer;

/**
 * Created by saipkri on 09/08/16.
 */
@Getter
public class InvokerConfigBuilder<I, O> {
    private String id;
    private Integer maxCapacity;
    private Consumer<TooManyInvocationsException> withTooManyInvocationsExceptionCallback;
    private Comparator<DataTuple<I, O>> withPriorityComputeFunction;
    private Integer withNoOfInvocationsParallel;

    public InvokerConfigBuilder<I, O> withId(final String id) {
        this.id = id;
        return this;
    }

    public InvokerConfigBuilder<I, O> withPriorityComputeFunction(final Comparator<DataTuple<I, O>> withPriorityComputeFunction) {
        this.withPriorityComputeFunction = withPriorityComputeFunction;
        return this;
    }

    public InvokerConfigBuilder<I, O> withTooManyInvocationsExceptionCallback(final Consumer<TooManyInvocationsException> withTooManyInvocationsExceptionCallback) {
        this.withTooManyInvocationsExceptionCallback = withTooManyInvocationsExceptionCallback;
        return this;
    }

    public InvokerConfigBuilder<I, O> withMaxCapacity(final Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
        return this;
    }

    public InvokerConfigBuilder<I, O> withNoOfInvocationsParallel(final Integer withNoOfInvocationsParallel) {
        this.withNoOfInvocationsParallel = withNoOfInvocationsParallel;
        return this;
    }

    public InvokerConfig<I, O> build() {
        InvokerConfig<I, O> config = new InvokerConfig<>();
        config.setId(id);
        config.setMaxCapacity(maxCapacity);
        config.setWithTooManyInvocationsExceptionCallback(withTooManyInvocationsExceptionCallback);
        config.setWithPriorityComputeFunction(withPriorityComputeFunction);
        config.setWithNoOfInvocationsParallel(withNoOfInvocationsParallel);
        return config;
    }
}
