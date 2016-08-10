package org.sai.tools.invoker.scratchpad;

import org.sai.tools.invoker.facade.InvokerFacade;
import org.sai.tools.invoker.factory.InvokerFactory;
import org.sai.tools.invoker.model.InvocationCostType;
import org.sai.tools.invoker.model.InvokerConfig;
import org.sai.tools.invoker.model.InvokerConfigBuilder;

import java.util.function.Function;

/**
 * Created by saipkri on 10/08/16.
 */
public class Client {

    public static void main(String[] args) throws Exception {

        InvokerConfig<Integer, String> throttlePolicy = new InvokerConfigBuilder<Integer, String>()
                .withId(InvocationCostType.HEAVY.toString())
                .withNoOfInvocationsParallel(8)
                .withPriorityComputeFunction((a, b) -> b.getData().compareTo(a.getData()))
                .withMaxCapacity(50)
                .withTooManyInvocationsExceptionCallback(Throwable::printStackTrace)
                .build();

        InvokerFacade<Integer, String> facade = InvokerFactory.getInstance().fromConfig(throttlePolicy);

        Function<Integer, String> actualFunction = n -> {
            System.out.println("\t Actual Throttled function called: " + n);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return " | " + n + " | ";
        };

        for (int i = 0; i < 5; i++) {
            facade.execute(i, actualFunction, System.out::println);
            //System.out.println(facade.executeWithNoPriority(i, actualFunction).get());
        }
    }
}
