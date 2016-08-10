package org.sai.tools.throttler.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by saipkri on 09/08/16.
 */
@Getter
@Setter
public class InvokerConfig<I, O> {
    private String id;
    private Function<I, InvocationCostType> withCostComputeFunction;
    private Integer maxCapacity;
    private Consumer<TooManyInvocationsException> withTooManyInvocationsExceptionCallback;
    private Comparator<DataTuple<I, O>> withPriorityComputeFunction;
    private Integer withNoOfInvocationsParallel;

    InvokerConfig() {
    }
}
