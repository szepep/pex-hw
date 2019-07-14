package com.szepe.peter.pex.rx;

import com.szepe.peter.pex.api.Try;
import rx.Observable;
import rx.Subscriber;

import java.util.function.Consumer;

public final class Operator<T, R, E extends Exception> implements Observable.Operator<R, T> {

    private final String name;
    private final Try.CheckedFunction<T, R, E> function;
    private final Consumer<Try.ExceptionAndValue<T, E>> exceptionConsumer;

    public static <T, R, E extends Exception> Operator<T, R, E> of(String name, Try.CheckedFunction<T, R, E> function) {
        return new Operator<>(name, function, null);
    }

    private Operator(String name, Try.CheckedFunction<T, R, E> function, Consumer<Try.ExceptionAndValue<T, E>> exceptionConsumer) {
        this.name = name;
        this.function = function;
        this.exceptionConsumer = exceptionConsumer;
    }

    public static <T, R, E extends Exception> Operator<T, R, E> of(String name, Try.CheckedFunction<T, R, E> function, Consumer<Try.ExceptionAndValue<T, E>> exceptionConsumer) {
        return new Operator<>(name, function, exceptionConsumer);
    }

    @Override
    public Subscriber<? super T> call(Subscriber<? super R> subscriber) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(throwable);
                }
            }

            @Override
            public void onNext(T t) {
                if (!subscriber.isUnsubscribed()) {
                    Try.tryIt(function).apply(t)
                            .consumeResult(subscriber::onNext)
                            .consumeException(exceptionConsumer);
                }
            }
        };
    }

}
