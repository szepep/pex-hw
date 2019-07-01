package com.szepe.peter.pex.api;

import java.util.function.Consumer;

public interface ExceptionConsumer<T, E extends Exception> {
    void consumeException(Consumer<Try.Pair<T, E>> exceptionConsumer);
}
