package utils;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Sean Grimes, sean@seanpgrimes.com
 *
 * Utility class to hold 2 values of the same type
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Pair<T> implements Serializable {
    // Version control for Serialization
    private static final long serialVersionUID = 1L;

    private T val1;
    private T val2;

    public Pair(T val1, T val2){
        this.val1 = val1;
        this.val2 = val2;
    }

    public T getVal1(){ return this.val1; }
    public T getVal2(){ return this.val2; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?> pair = (Pair<?>) o;
        return getVal1().equals(pair.getVal1()) &&
                getVal2().equals(pair.getVal2());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVal1(), getVal2());
    }

    @Override
    public String toString(){
        return val1 + " - " + val2;
    }
}
