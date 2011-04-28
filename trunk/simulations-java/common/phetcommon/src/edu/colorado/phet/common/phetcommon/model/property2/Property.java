// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property2;

/**
 * @author Sam Reid
 */
public class Property<T> extends Observable<T> {
    protected Property( T value ) {
        super( value );
    }

    //makes setter public
    @Override public void setValue( T newValue ) {
        super.setValue( newValue );
    }
}
