package com.szepe.peter.pex.api;

public class ComparablePairByValue<K, V extends Comparable<V>> implements Comparable<ComparablePairByValue<K, V>> {

    private final K k;
    private final V v;

    public ComparablePairByValue(K k, V v) {
        this.k = k;
        this.v = v;
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
