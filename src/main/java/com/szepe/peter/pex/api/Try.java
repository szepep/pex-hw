package com.szepe.peter.pex.api;

import java.util.function.Consumer;
import java.util.function.Function;

public class Try<T, R, E extends Exception> implements ResultConsumer<R>, ExceptionConsumer<T, E> {

    public static <T, R, E extends Exception> Function<T, Try<T, R, E>> tryIt(CheckedFunction<T, R> function) {
        return t -> {
            try {
                return Try.result(function.apply(t));
            } catch (Exception ex) {
                return Try.exception(t, (E) ex);
            }
        };
    }

    private static <T, R, E extends Exception> Try<T, R, E> exception(T t, E e) {
        return new Try<>(null, new Pair<>(t, e));
    }

    private static <T, R, E extends Exception> Try<T, R, E> result(R r) {
        return new Try<>(r, null);
    }
    private final R r;

    private final Pair<T, E> p;

    private Try(R r, Pair<T, E> p) {
        this.r = r;
        this.p = p;
    }

    @Override
    public ExceptionConsumer<T, E> consumeResult(Consumer<R> consumer) {
        if (r != null) {
            consumer.accept(r);
        }
        return this;
    }

    @Override
    public void consumeException(Consumer<Pair<T, E>> exceptionConsumer) {
        if (p != null) {
            exceptionConsumer.accept(p);
        }
    }

    public static class Pair<T, E extends Exception> {
        private final T value;

        private final E exception;

        private Pair(T value, E exception) {
            this.value = value;
            this.exception = exception;
        }

        public T getValue() {
            return value;
        }
        public E getException() {
            return exception;
        }

    }
    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws Exception;
    }
}
