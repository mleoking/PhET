package edu.colorado.phet.common.phetcommon.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * This adapter class converts an enumeration property to a boolean property indicating
 * true if the specified property has the specified value.
 * <p>
 * Note that this class is not recommend for radio button handlers; use PropertyRadioButton.
 *
 * @param <T> the property value type
 * @author Sam Reid
 */
public class IsSelected<T> extends BooleanProperty {
    
    public IsSelected( final Property<T> property, final T value ) {
        super( property.getValue() == value );
        property.addObserver( new SimpleObserver() {
            public void update() {
                setValue( property.getValue().equals( value ) );
            }
        } );
        addObserver( new SimpleObserver() {
            public void update() {
                if ( getValue() ) {
                    property.setValue( value );
                }
            }
        } );
    }
}
