package edu.colorado.phet.gravityandorbits.model;

import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * @author Sam Reid
 */
public class PublicProperty<T> extends Property<T> {
    public PublicProperty( T value ) {
        super( value );
    }

    @Override
    public T getDefaultValue() {
        return super.getDefaultValue();
    }
}
