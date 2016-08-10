## A Simple layer to serve as a facade to the method calls driven by various policies. ##
The calls can be
* Allow N concurrent requests at a time.
* The inputs can be prioritized (input A can be processed before B although B may be the first one to be queued).
* Maximum requests that can be queued.
* Callback features.
* Java Futures supported.

### To use it ###
#### Step 1: Create a Invocation Config ####
This is the invocation config:
```
InvokerConfig<Integer, String> InvokerPolicy = new InvokerConfigBuilder<Integer, String>()
                .withId(InvocationCostType.HEAVY.toString())
                .withNoOfInvocationsParallel(8)
                .withPriorityComputeFunction((a, b) -> b.getData().compareTo(a.getData()))
                .withMaxCapacity(50)
                .withTooManyInvocationsExceptionCallback(Throwable::printStackTrace)
                .build();
```
Each config needs an id for it to be cached and reused.
```
withNoOfInvocationsParallel --> Number of concurrent invocations allowed.
withPriorityComputeFunction --> A comparator function that determines the priority of the input to the invoked method.
withMaxCapacity --> How many maximum number of requests can be queued for processing. Limit this number for low memory constraints.
withTooManyInvocationsExceptionCallback --> When there are too many requests beyond the capacity of the queue, this callback is invoked with the Exception.
```

#### Step 2: Get a InvocationFacade  ####
```
InvokerFacade<Integer, String> facade = InvokerrFactory.getInstance().fromConfig(InvokerPolicy);
// This is the function we want to invoke (Just a trivial function).
Function<Integer, String> actualFunction = n -> " | " + n + " | ";
// This is how we call it.
// First argument is the actual function to be invoked
// Second argument is the callback when the result is returned by the invoked function.
facade.execute(i, actualFunction, System.out::println);
```

**You can also get a handle to a Future**
```
// It returns a future, hence you don't need to pass a callback function.
Future<String> result = facade.executeWithNoPriority(i, actualFunction);
```

**To use it in your IDE (Eclipse, intelliJ etc) please install Lombok Plugin**


