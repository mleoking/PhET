//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
/**
 * This function is used to clamp a value to be another value, used in PropertyEditor so that values don't go out of range.
 * One reason values must be kept in a certain range is so that the pool cannot be overflowed.
 */
public interface Bounds {
    function clamp( value: Number ): Number;
}
}