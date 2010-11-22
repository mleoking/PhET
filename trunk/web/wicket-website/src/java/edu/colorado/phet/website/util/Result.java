package edu.colorado.phet.website.util;

import java.io.Serializable;

/**
 * Enables wrapping of any result type within an inner class.
 * <p/>
 * TODO: add success flag?
 * TODO: use instead of arrays
 *
 * @param <T>
 */
public class Result<T> implements Serializable {
    private T value;

    public Result() {
    }

    public Result( T value ) {
        this.value = value;
    }

    public void setValue( T value ) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
