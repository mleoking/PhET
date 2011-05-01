// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property2;

/**
 * This adapter class converts an enumeration property to a boolean property indicating
 * true if the specified property's value equals the specified value.
 * <p/>
 * Note that this class is not recommend for radio button handlers; use PropertyRadioButton.
 *
 * @param <T> the property value type
 * @author Sam Reid
 */
public class ValueEquals<T> extends Property<Boolean> {
    private T value;
    private Observable<T> property;

    public ValueEquals( final Observable<T> property, final T value ) {
        super( property.getValue().equals( value ) );
        this.value = value;
        this.property = property;
        //Send out notifications, being careful not to send duplicate notifications when there wasn't actually a change in getValue()
        property.addObserver( new Observer<T>() {
            @Override public void update( UpdateEvent<T> tUpdateEvent ) {
                setValue( property.getValue().equals( value ) );
            }
        } );
    }

    //Returns a property that is an 'or' conjunction of this and the provided argument
//    public Or or( ObservableProperty<Boolean> p ) {
//        return new Or( this, p );
//    }

    @Override
    public Boolean getValue() {
        return property.getValue() == value;
    }
}
