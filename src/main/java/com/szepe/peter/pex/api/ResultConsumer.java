package com.szepe.peter.pex.api;

import java.util.function.Consumer;

public interface ResultConsumer<R> {
    ExceptionConsumer consumeResult(Consumer<R> resultConsumer);
}
