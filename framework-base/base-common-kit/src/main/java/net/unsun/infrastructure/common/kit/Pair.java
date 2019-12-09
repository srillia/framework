package net.unsun.infrastructure.common.kit;

import java.io.Serializable;

/**
 * Key - Value 实现
 *
 * @author Tokey
 * @version 1.0.0
 * @since 2017/09/12 20:29
 */
public class Pair<K, V> implements Serializable {

    private K key;

    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }

    @Override
    public int hashCode() {
        return key.hashCode() * 13 + (value == null ? 0 : value.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            {
                return true;
            }
        }
        if (o instanceof Pair) {
            Pair pair = (Pair) o;
            if (key != null ? !key.equals(pair.key) : pair.key != null) {
                {
                    return false;
                }
            }
            if (value != null ? !value.equals(pair.value) : pair.value != null) {
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
