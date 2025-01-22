package utils;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Sean Grimes, sean@seanpgrimes.com
 *
 * Utility class to hold 3 values of the same type
 */
public class Triple<T> implements Serializable {
    // Version control for Serialization
    private static final long serialVersionUID = 1L;

    private T val1;
    private T val2;
    private T val3;

    public Triple(T val1, T val2, T val3){
        this.val1 = val1;
        this.val2 = val2;
        this.val3 = val3;
    }

    public T getVal1(){ return this.val1; }
    public T getVal2(){ return this.val2; }
    public T getVal3(){ return this.val3; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Triple)) return false;
        Triple<?> triple = (Triple<?>) o;
        return getVal1().equals(triple.getVal1()) &&
                getVal2().equals(triple.getVal2()) &&
                getVal3().equals(triple.getVal3());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVal1(), getVal2(), getVal3());
    }

    @Override
    public String toString(){
        return val1 + " - " + val2 + " - " + val3;
    }
}
