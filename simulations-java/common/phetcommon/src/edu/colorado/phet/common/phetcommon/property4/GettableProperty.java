// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.property4;

/**
 * A property that is publicly gettable, but not publicly settable.
 * Subclasses may change the value, or may choose to make the value publicly settable.
 * If the value does change, listeners are notified.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GettableProperty<T> extends PropertyChangeNotifier {

    private T initialValue, value, oldValue;

    public GettableProperty( T value ) {
        this.value = value;
        this.oldValue = null;
    }

    public T getValue() {
        return value;
    }

    /**
     * If the value is different, sets the value and notifies listeners.
     * This is protected because subclasses should be able to set the value, but clients should not.
     *
     * @param value
     */
    protected void setValue( T value ) {
        if ( !value.equals( this.value ) ) {
            this.oldValue = this.value;
            this.value = value;
            firePropertyChanged( oldValue, value );
        }
    }

    /**
     * Gets the value provided when the property was constructed.
     *
     * @return
     */
    public T getInitialValue() {
        return initialValue;
    }

    /**
     * Resets the property to its initial value.
     */
    public void reset() {
        setValue( initialValue );
    }

    public static void main( String[] args ) {
        GettableProperty<Integer> age = new GettableProperty<Integer>( 40 );
        assert ( age.getValue() == 40 );
        System.out.println( "age=" + age.getValue() );
    }
}
