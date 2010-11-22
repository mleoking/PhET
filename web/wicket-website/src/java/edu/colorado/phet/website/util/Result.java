package edu.colorado.phet.website.util;

/**
 * Enables wrapping of any result type within an inner class.
 * <p/>
 * TODO: add success flag
 *
 * @param <T>
 */
public class Result<T> {
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
