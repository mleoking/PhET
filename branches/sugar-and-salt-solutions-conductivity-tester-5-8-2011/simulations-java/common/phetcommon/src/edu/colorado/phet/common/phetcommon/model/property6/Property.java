//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property6;

/**
 * A minimalistic Property, implemented due to peer pressure. Will remove it when the other temporary property packages are done.
 * @param <T> A class representing something T-shaped
 */
public class Property<T> {
    public final T value;

    public Property( T value ) {
        this.value = value;
    }
}
