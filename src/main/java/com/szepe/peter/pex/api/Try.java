package com.szepe.peter.pex.api;

import java.util.function.Consumer;
import java.util.function.Function;

public class Try<T, R, E extends Exception> {

    public static <T, R, E extends Exception> Function<T, Try<T, R, E>> tryIt(CheckedFunction<T, R, E> function) {
        return t -> {
            try {
                return Try.result(function.apply(t));
            } catch (Exception ex) {
                return Try.exception(t, (E) ex);
            }
        };
    }

    private static <T, R, E extends Exception> Try<T, R, E> exception(T t, E e) {
        return new Try<>(null, new ExceptionPair<>(t, e));
    }

    private static <T, R, E extends Exception> Try<T, R, E> result(R r) {
        return new Try<>(r, null);
    }
    private final R r;

    private final ExceptionPair<T, E> p;

    private Try(R r, ExceptionPair<T, E> p) {
        this.r = r;
        this.p = p;
    }

    public ExceptionConsumer<T, E> consumeResult(Consumer<R> consumer) {
        if (r != null) {
            consumer.accept(r);
        }
        return this::consumeException;
    }

    public void consumeException(Consumer<ExceptionPair<T, E>> exceptionConsumer) {
        if (p != null) {
            if (exceptionConsumer != null) {
                exceptionConsumer.accept(p);
            }
        }
    }

    public static class ExceptionPair<T, E extends Exception> {
        private final T value;

        private final E exception;

        private ExceptionPair(T value, E exception) {
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
    public interface CheckedFunction<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    @FunctionalInterface
    public interface ExceptionConsumer<T, E extends Exception> {
        void consumeException(Consumer<Try.ExceptionPair<T, E>> exceptionConsumer);
    }

}
