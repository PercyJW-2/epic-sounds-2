package util;

public class Pair<K, V> {
    private K first;
    private V second;

    public Pair(final K first, final V second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }

    public void setFirst(final K first) {
        this.first = first;
    }

    public V getSecond() {
        return second;
    }

    public void setSecond(final V second) {
        this.second = second;
    }
}
