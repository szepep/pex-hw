package com.szepe.peter.pex.rx;

import com.szepe.peter.pex.api.Try;
import com.szepe.peter.pex.utils.Watch;
import rx.Observable;
import rx.Subscriber;

import java.util.function.Consumer;
import java.util.logging.Logger;

public class Operator<T, R, E extends Exception> implements Observable.Operator<R, T> {

    private final static Logger logger = Logger.getLogger(Operator.class.getName());

    private final Watch watch = new Watch();
    private final String name;
    private final Try.CheckedFunction<T, R, E> function;
    private final Consumer<Try.ExceptionPair<T, E>> exceptionConsumer;

    public static <T, R, E extends Exception> Operator<T, R, E> of(String name, Try.CheckedFunction<T, R, E> function) {
        return new Operator<>(name, function, null);
    }

    public static <T, R, E extends Exception> Operator<T, R, E> of(String name, Try.CheckedFunction<T, R, E> function, Consumer<Try.ExceptionPair<T, E>> exceptionConsumer) {
        return new Operator<>(name, function, exceptionConsumer);
    }

    private Operator(String name, Try.CheckedFunction<T, R, E> function, Consumer<Try.ExceptionPair<T, E>> exceptionConsumer) {
        this.name = name;
        this.function = function;
        this.exceptionConsumer = exceptionConsumer;
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
                    watch.start(function, t)
                            .consumeResult(subscriber::onNext)
                            .consumeException(exceptionConsumer);
                }
            }
        };
    }

    public String printStats() {
        return name + ":\n" + watch.prettyPrint();
    }
}
