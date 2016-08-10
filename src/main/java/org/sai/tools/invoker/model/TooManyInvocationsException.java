package org.sai.tools.invoker.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by saipkri on 09/08/16.
 */
@Data
@AllArgsConstructor
public class TooManyInvocationsException extends RuntimeException {
    private final String message;
}
