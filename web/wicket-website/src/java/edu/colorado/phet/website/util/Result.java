package edu.colorado.phet.website.util;

/**
 * Enables wrapping of any result type within an inner class.
 * <p/>
 * TODO: add success flag
 *
 * @param <T>
 */
public class Result<T> {
    private T object;

    public Result() {
    }

    public Result( T object ) {
        this.object = object;
    }

    public void setObject( T object ) {
        this.object = object;
    }

    public T getObject() {
        return object;
    }
}
