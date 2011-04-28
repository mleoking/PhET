// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property2;

/**
 * @author Sam Reid
 */
public class PhetProperty<T> extends PhetObservable<T> {
    protected PhetProperty( T value ) {
        super( value );
    }

    //makes setter public
    @Override public void setValue( T newValue ) {
        super.setValue( newValue );
    }
}
