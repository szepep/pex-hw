package com.szepe.peter.pex.api;

public class ComparablePairByValue<K, V extends Comparable<V>> implements Comparable<ComparablePairByValue<K, V>> {

    public static <K, V extends Comparable<V>> ComparablePairByValue<K, V> of(K k, V v) {
        return new ComparablePairByValue<>(k, v);
    }

    private final K k;
    private final V v;

    public ComparablePairByValue(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public K getK() {
        return k;
    }

    public V getV() {
        return v;
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
