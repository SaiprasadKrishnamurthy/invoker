package org.sai.tools.throttler.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Consumer;

@Data
@AllArgsConstructor
public class DataTuple<I, O> {
    private final I data;
    private final Consumer<O> resultCallback;
}