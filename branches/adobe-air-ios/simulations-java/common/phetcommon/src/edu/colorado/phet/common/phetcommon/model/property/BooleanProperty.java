// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property;

/**
 * This can be used to represent a Boolean value in a MVC style pattern.  It remembers its default value and can be reset.
 *
 * @author Sam Reid
 */
public class BooleanProperty extends Property<Boolean> {
    public BooleanProperty( Boolean value ) {
        super( value );
    }

    public And and( ObservableProperty<Boolean> p ) {
        return new And( this, p );
    }

    public Or or( ObservableProperty<Boolean> p ) {
        return new Or( this, p );
    }
}
