package com.szepe.peter.pex.api;

public class Pair<F, S> {

    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair(first, second);
    }

    private final F first;
    private final S second;

    private Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
