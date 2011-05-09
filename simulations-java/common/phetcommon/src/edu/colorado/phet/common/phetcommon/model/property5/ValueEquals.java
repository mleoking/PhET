// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property5;

import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * This adapter class converts an enumeration property to a boolean property indicating
 * true if the specified property's value equals the specified value.
 * <p/>
 * Note that this class is not recommend for radio button handlers; use PropertyRadioButton.
 *
 * @param <T> the property value type
 * @author Sam Reid
 */
public class ValueEquals<T> extends CompositeProperty<Boolean> {
    public ValueEquals( final Property<T> property, final T value ) {
        super( new Function0<Boolean>() {
            public Boolean apply() {
                return property.getValue().equals( value );
            }
        }, property );
    }

    //REVIEW why is this here?
    //Returns a property that is an 'or' conjunction of this and the provided argument
    public Or or( ObservableProperty<Boolean> p ) {
        return new Or( this, p );
    }
}