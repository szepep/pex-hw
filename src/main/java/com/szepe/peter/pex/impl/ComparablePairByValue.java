package com.szepe.peter.pex.impl;

import com.google.common.base.Preconditions;

import java.util.Objects;

class ComparablePairByValue<K, V extends Comparable<V>> implements Comparable<ComparablePairByValue<K, V>> {

    public static <K, V extends Comparable<V>> ComparablePairByValue<K, V> of(K k, V v) {
        return new ComparablePairByValue<>(k, v);
    }

    private final K k;
    private final V v;

    private ComparablePairByValue(K k, V v) {
        this.k = Preconditions.checkNotNull(k, "The key cannot be null");
        this.v = Preconditions.checkNotNull(v, "The value cannot be null");
    }

    public K getK() {
        return k;
    }

    public V getV() {
        return v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComparablePairByValue<?, ?> that = (ComparablePairByValue<?, ?>) o;
        return k.equals(that.k) &&
                v.equals(that.v);
    }

    @Override
    public int hashCode() {
        return Objects.hash(k, v);
    }

    @Override
    public int compareTo(ComparablePairByValue<K, V> o) {
        return this.v.compareTo(o.v);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "k=" + k +
                ", v=" + v +
                '}';
    }
}
