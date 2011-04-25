// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * This adapter class converts an enumeration property to a boolean property indicating
 * true if the specified property's value equals the specified value.
 * <p/>
 * Note that this class is not recommend for radio button handlers; use PropertyRadioButton.
 *
 * @param <T> the property value type
 * @author Sam Reid
 */
public class ValueEquals<T> extends ObservableProperty<Boolean> {
    private T value;
    private Property<T> property;
    private boolean valueAtLastNotification;

    public ValueEquals( final Property<T> property, final T value ) {
        this.value = value;
        this.property = property;
        //Send out notifications, being careful not to send duplicate notifications when there wasn't actually a change in getValue()
        property.addObserver( new SimpleObserver() {
            public void update() {
                if ( getValue() != valueAtLastNotification ) {
                    notifyObservers( getValue(), valueAtLastNotification );
                    valueAtLastNotification = getValue();
                }
            }
        } );
        valueAtLastNotification = getValue();
    }

    //Returns a property that is an 'or' conjunction of this and the provided argument
    public Or or( ObservableProperty<Boolean> p ) {
        return new Or( this, p );
    }

    @Override
    public Boolean getValue() {
        return property.getValue() == value;
    }
}
