package edu.colorado.phet.website.util.hibernate;

import java.io.Serializable;

/**
 * Enables wrapping of any result type within an inner class.
 * <p/>
 * TODO: add success flag?
 * TODO: use instead of arrays
 *
 * @param <T>
 */
public class HibernateResult<T> implements Serializable {
    private T value;

    public HibernateResult() {
    }

    public HibernateResult( T value ) {
        this.value = value;
    }

    public void setValue( T value ) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
